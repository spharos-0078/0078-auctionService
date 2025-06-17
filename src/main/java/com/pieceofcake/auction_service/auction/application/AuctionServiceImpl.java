package com.pieceofcake.auction_service.auction.application;

import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadHighestBidPriceResponseDto;
import com.pieceofcake.auction_service.auction.dto.out.UpdateAuctionPriceSseDto;
import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import com.pieceofcake.auction_service.auction.infrastructure.AuctionRepository;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService{

    private final AuctionRepository auctionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic auctionPriceTopic;
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

        if (auction.getAuctionStatus() != AuctionStatus.ONGOING) return;

        // 종료 로직 (낙찰자 결정, 상태 변경 등)
        Auction newAuction = Auction.builder()
                .id(auction.getId())
                .auctionUuid(auction.getAuctionUuid())
                .productUuid(auction.getProductUuid())
                .startingPrice(auction.getStartingPrice())
                .highestBidUuid(auction.getHighestBidUuid())
                .highestBidPrice(auction.getHighestBidPrice())
                .highestBidMemberUuid(auction.getHighestBidMemberUuid())
                .startDate(auction.getStartDate())
                .endDate(auction.getEndDate())
                .auctionStatus(AuctionStatus.CLOSED)
                .build();

        auctionRepository.save(newAuction);
    }

}
