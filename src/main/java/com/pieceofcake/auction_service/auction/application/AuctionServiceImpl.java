package com.pieceofcake.auction_service.auction.application;

import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
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
                kafkaProducer.sendAuctionStartEvent(auction.getProductUuid());
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
                () -> {
                    try {
                        AuctionService proxy = applicationContext.getBean(AuctionService.class);
                        proxy.closeAuction(auction.getAuctionUuid());
//                        closeAuction(auction.getAuctionUuid());
                        log.info("자동 종료된 경매: {}", auction.getAuctionUuid());
                    } catch (Exception e) {
                        log.error("경매 자동 종료 실패: {}", auction.getAuctionUuid(), e);
                    }
                },
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
        log.info("updateAuction: {}, 최고 입찰자: {}, 최고 입찰가: {}", auction.getAuctionUuid(), oldHighestMemberUuid, oldHighestBidPrice);

        if (auction.getAuctionStatus() != AuctionStatus.ONGOING) {
            throw new BaseException(BaseResponseStatus.AUCTION_NOT_ONGOING);
        }

        // 엔티티 업데이트
        auctionRepository.save(updateAuctionDto.updateEntity(auction));

//        // 기존 최고가가 있으면, 해당 유저의 보증금 없애기
        if (auction.getHighestBidUuid() != null) {
            // 첫 번째 요청을 보내고 완료될 때까지 기다립니다
            CompletableFuture<Void> refundFuture = CompletableFuture.runAsync(() -> {
                auctionFeignClient.createMoneyWithMemberUuid(
                        CreateMoneyWithMemberUuidRequestDto.builder()
                                .memberUuid(oldHighestMemberUuid)
                                .amount(oldHighestBidPrice)
                                .isPositive(true)
                                .historyType(MoneyHistoryType.FREEZE)
                                .moneyHistoryDetail(auction.getAuctionUuid())
                                .build()
                );
            });
            try {
                // 첫 번째 요청이 완료될 때까지 기다린 후 두 번째 요청을 실행합니다
                refundFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("이전 입찰자의 보증금 반환 중 오류 발생: {}", e.getMessage());
                throw new BaseException(BaseResponseStatus.MONEY_SERVICE_ERROR);
            }
        }
        // 현재 입찰자의 보증금 추가
        auctionFeignClient.createMoney(
                CreateMoneyRequestDto.builder()
                        .amount(updateAuctionDto.getBidPrice())
                        .isPositive(false)
                        .historyType(MoneyHistoryType.FREEZE)
                        .moneyHistoryDetail(auction.getAuctionUuid())
                        .build()
        );

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

        if (auction.getHighestBidUuid().equals(newAuction.getHighestBidUuid())){
            // 최고입찰자와 bid 기준 입찰자가 다르면

            // auction의 입찰자는 환불
            auctionFeignClient.createMoneyWithMemberUuid(
                    CreateMoneyWithMemberUuidRequestDto.builder()
                            .memberUuid(oldHighestMemberUuid)
                            .amount(oldHighestBidPrice)
                            .isPositive(true)
                            .historyType(MoneyHistoryType.FREEZE)
                            .moneyHistoryDetail(auction.getAuctionUuid())
                            .build()
            );

            // bid의 입찰자에게 구매 진행
            auctionFeignClient.createMoneyWithMemberUuid(
                    CreateMoneyWithMemberUuidRequestDto.builder()
                            .memberUuid(highestBid.getMemberUuid())
                            .isPositive(false)
                            .amount(highestBid.getBidPrice())
                            .historyType(MoneyHistoryType.PRODUCT_BUY)
                            .moneyHistoryDetail(auction.getAuctionUuid())
                            .build()
            );
        } else {
            // 최고입찰자가 bid와 같으면
            auctionFeignClient.createMoneyWithMemberUuid(
                    CreateMoneyWithMemberUuidRequestDto.builder()
                            .memberUuid(auction.getHighestBidMemberUuid())
                            .amount(auction.getHighestBidPrice())
                            .isPositive(false)
                            .historyType(MoneyHistoryType.PRODUCT_BUY)
                            .moneyHistoryDetail(auction.getAuctionUuid())
                            .build()
            );
        }

        auctionRepository.save(newAuction);


        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("경매 종료 이벤트 발행: @@@@@@@@{}", auction.getProductUuid());
                kafkaProducer.sendAuctionCloseEvent(auction.getProductUuid());
            }
        });
    }

}
