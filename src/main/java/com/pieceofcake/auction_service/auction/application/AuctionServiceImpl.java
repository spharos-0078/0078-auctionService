package com.pieceofcake.auction_service.auction.application;

import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadHighestBidPriceResponseDto;
import com.pieceofcake.auction_service.auction.dto.out.UpdateAuctionPriceSseDto;
import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import com.pieceofcake.auction_service.auction.infrastructure.AuctionRepository;
import com.pieceofcake.auction_service.bid.entity.Bid;
import com.pieceofcake.auction_service.bid.infrastructure.BidRepository;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService{

    private final AuctionRepository auctionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic auctionPriceTopic;
    private final BidRepository bidRepository;
    private final TaskScheduler taskScheduler;

    @Override
    public ReadHighestBidPriceResponseDto readHighestBid(ReadHighestBidPriceRequestDto readHighestBidPriceRequestDto) {
        Auction auction = auctionRepository.findByAuctionUuid(readHighestBidPriceRequestDto.getAuctionUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AUCTION_NOT_FOUND));

        return ReadHighestBidPriceResponseDto.from(auction);
    }

    @Override
    @Transactional
    public void createAuction(CreateAuctionRequestDto createAuctionRequestDto) {
        Auction auction = createAuctionRequestDto.toEntity();
        auctionRepository.save(auction);
    }

    @Override
    public void scheduleAuctionClose(Auction auction) {
        if (auction.getEndDate().isBefore(java.time.LocalDateTime.now())) {
            log.warn("경매 종료 시간이 현재보다 이전입니다: {}", auction.getAuctionUuid());
            return;
        }

        taskScheduler.schedule(
                () -> {
                    try {
                        closeAuction(auction.getAuctionUuid());
                        log.info("자동 종료된 경매: {}", auction.getAuctionUuid());
                    } catch (Exception e) {
                        log.error("경매 자동 종료 실패: {}", auction.getAuctionUuid(), e);
                    }
                },
                java.util.Date.from(auction.getEndDate().atZone(java.time.ZoneId.systemDefault()).toInstant())
        );
    }

    @Override
    @Transactional
    public void updateAuction(UpdateAuctionDto updateAuctionDto) {
        Auction auction = auctionRepository.findByAuctionUuid(updateAuctionDto.getAuctionUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AUCTION_NOT_FOUND));

        if (auction.getAuctionStatus() != AuctionStatus.ONGOING) {
            throw new BaseException(BaseResponseStatus.AUCTION_NOT_ONGOING);
        }

        // 엔티티 업데이트
        auctionRepository.save(updateAuctionDto.updateEntity(auction));

        // SSE 이벤트 발행
        UpdateAuctionPriceSseDto updateAuctionPriceSseDto = UpdateAuctionPriceSseDto.builder()
                .auctionUuid(updateAuctionDto.getAuctionUuid())
                .bidPrice(updateAuctionDto.getBidPrice())
                .bidUuid(updateAuctionDto.getBidUuid())
                .memberUuid(updateAuctionDto.getMemberUuid())
                .build();

        // Redis를 통해 이벤트 발행
        redisTemplate.convertAndSend(auctionPriceTopic.getTopic(), updateAuctionPriceSseDto);
    }

    public void closeAuction(String auctionUuid) {
        Auction auction = auctionRepository.findByAuctionUuid(auctionUuid)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AUCTION_NOT_FOUND));

        // 경매 상태가 ONGOING이 아닐 경우 에러
        if (auction.getAuctionStatus() != AuctionStatus.ONGOING) {
            throw new BaseException(BaseResponseStatus.AUCTION_NOT_ONGOING);
        }

        // 경매 종료 시간이 현재 시간보다 이전일 경우 에러
        if (LocalDateTime.now().isBefore(auction.getEndDate())) {
            log.warn("스케줄러가 너무 빨리 실행됨. 다시 예약: {}", auctionUuid);
            scheduleAuctionClose(auction);
            return;
        }

        // 1. bid 테이블에서 최고가 입찰 조회
        Bid highestBid = bidRepository.findFirstByAuctionUuidOrderByBidPriceDesc(auctionUuid)
                .orElse(null);

        // 2. 종료 로직 (낙찰자 결정, 상태 변경 등)
        Auction newAuction = Auction.builder()
                .id(auction.getId())
                .auctionUuid(auction.getAuctionUuid())
                .productUuid(auction.getProductUuid())
                .startingPrice(auction.getStartingPrice())
                .highestBidUuid(highestBid != null ? highestBid.getBidUuid() : null)
                .highestBidPrice(highestBid != null ? highestBid.getBidPrice() : null)
                .highestBidMemberUuid(highestBid != null ? highestBid.getMemberUuid() : null)
                .startDate(auction.getStartDate())
                .endDate(auction.getEndDate())
                .auctionStatus((highestBid != null) ? AuctionStatus.CLOSED : AuctionStatus.NO_BID)
                .build();

        auctionRepository.save(newAuction);
    }

}
