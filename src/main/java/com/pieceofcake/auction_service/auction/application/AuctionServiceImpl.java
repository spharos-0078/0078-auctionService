package com.pieceofcake.auction_service.auction.application;

import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadAuctionListResponseDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadHighestBidPriceResponseDto;
import com.pieceofcake.auction_service.auction.dto.out.UpdateAuctionPriceSseDto;
import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import com.pieceofcake.auction_service.auction.infrastructure.AuctionRepository;
import com.pieceofcake.auction_service.auction.infrastructure.client.AuctionFeignClient;
import com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.CreateMoneyRequestDto;
import com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.CreateMoneyWithMemberUuidRequestDto;
import com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.enums.MoneyHistoryType;
import com.pieceofcake.auction_service.bid.entity.Bid;
import com.pieceofcake.auction_service.bid.infrastructure.BidRepository;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import com.pieceofcake.auction_service.kafka.producer.KafkaProducer;
import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService{

    private final AuctionRepository auctionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic auctionPriceTopic;
    private final BidRepository bidRepository;
    private final TaskScheduler taskScheduler;
    private final AuctionFeignClient auctionFeignClient;
    private final KafkaProducer kafkaProducer;
    private final ApplicationContext applicationContext;
    private final AuctionSagaService auctionSagaService;
    private final AuctionSchedulerService auctionSchedulerService;


    @Override
    public ReadHighestBidPriceResponseDto readHighestBid(ReadHighestBidPriceRequestDto readHighestBidPriceRequestDto) {
        Auction auction = auctionRepository.findByAuctionUuid(readHighestBidPriceRequestDto.getAuctionUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AUCTION_NOT_FOUND));

        return ReadHighestBidPriceResponseDto.from(auction);
    }

    @Override
    @Transactional
    public void createAuction(CreateAuctionRequestDto createAuctionRequestDto) {
        if (auctionRepository.existsByProductUuid(createAuctionRequestDto.getProductUuid())) {
            throw new BaseException(BaseResponseStatus.AUCTION_ALREADY_EXISTS);
        }
        Auction auction = createAuctionRequestDto.toEntity();
        auctionRepository.save(auction);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaProducer.sendAuctionStartEvent(auction.getPieceProductUuid());
            }
        });
    }

    @Override
    public void scheduleAuctionClose(Auction auction) {
        if (auction.getEndDate().isBefore(java.time.LocalDateTime.now())) {
            log.warn("경매 종료 시간이 현재보다 이전입니다: {}", auction.getAuctionUuid());
            return;
        }

        taskScheduler.schedule(
                () -> auctionSchedulerService.closeAuctionWithNewTransaction(auction.getAuctionUuid()),
                auction.getEndDate().atZone(java.time.ZoneId.systemDefault()).toInstant()
        );
    }

    @Override
    @Transactional
    public void updateAuction(UpdateAuctionDto updateAuctionDto) {
        Auction auction = auctionRepository.findByAuctionUuid(updateAuctionDto.getAuctionUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AUCTION_NOT_FOUND));

        String oldHighestMemberUuid = auction.getHighestBidMemberUuid() != null ?
                auction.getHighestBidMemberUuid() : "";
        Long oldHighestBidPrice = auction.getHighestBidPrice() != null ?
                auction.getHighestBidPrice() : 0L;

        if (auction.getAuctionStatus() != AuctionStatus.ONGOING) {
            throw new BaseException(BaseResponseStatus.AUCTION_NOT_ONGOING);
        }

        // 엔티티 업데이트
        auctionRepository.save(updateAuctionDto.updateEntity(auction));

        // Saga 패턴을 사용한 보증금 처리
        auctionSagaService.processAuctionUpdateWithSaga(updateAuctionDto, oldHighestMemberUuid, oldHighestBidPrice);

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


    @Transactional
    @Override
    public void closeAuction(String auctionUuid) {
        Auction auction = auctionRepository.findByAuctionUuid(auctionUuid)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AUCTION_NOT_FOUND));

        String oldHighestMemberUuid = auction.getHighestBidMemberUuid() != null ?
                auction.getHighestBidMemberUuid() : "";
        Long oldHighestBidPrice = auction.getHighestBidPrice() != null ?
                auction.getHighestBidPrice() : 0L;

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

        log.info("경매 종료: {}, 최고 입찰: {}", auctionUuid, highestBid.getBidUuid());

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

        // Saga 패턴을 사용한 경매 종료 처리
        if (highestBid != null) {
            auctionSagaService.processAuctionCloseWithSaga(
                auctionUuid, 
                highestBid.getMemberUuid(), 
                highestBid.getBidPrice()
            );
        }

        auctionRepository.save(newAuction);


        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("경매 종료 이벤트 발행: @@@@@@@@{}", auction.getPieceProductUuid());
                kafkaProducer.sendAuctionCloseEvent(auction.getPieceProductUuid());
            }
        });
    }

    @Override
    public List<ReadAuctionListResponseDto> readAuctionList(String status) {
        List<Auction> auctions;

        if (status != null && !status.isEmpty()) {
            try {
                AuctionStatus auctionStatus = AuctionStatus.valueOf(status);
                auctions = auctionRepository.findAllByAuctionStatus(auctionStatus);
            } catch (IllegalArgumentException e) {
                // 유효하지 않은 status 값이 전달된 경우
                throw new BaseException(BaseResponseStatus.INVALID_AUCTION_STATUS);
            }
        } else {
            auctions = auctionRepository.findAll();
        }

        return auctions
                .stream()
                .map(ReadAuctionListResponseDto::from)
                .toList();
    }

}
