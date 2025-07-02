package com.pieceofcake.auction_service.auction.application;

import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.infrastructure.client.AuctionFeignClient;
import com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.CreateMoneyWithMemberUuidRequestDto;
import com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.enums.MoneyHistoryType;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionSagaService {
    
    private final AuctionFeignClient auctionFeignClient;
    
    /**
     * Saga 패턴을 사용한 경매 업데이트 처리
     * 1. 이전 입찰자 보증금 반환
     * 2. 현재 입찰자 보증금 차감
     * 3. 실패 시 보상 트랜잭션 실행
     */
    @Transactional
    public void processAuctionUpdateWithSaga(UpdateAuctionDto updateAuctionDto, 
                                           String oldHighestMemberUuid, 
                                           Long oldHighestBidPrice) {
        
        // Step 1: 이전 입찰자 보증금 반환 (필요한 경우)
        CompletableFuture<Void> refundFuture = null;
        if (oldHighestMemberUuid != null && !oldHighestMemberUuid.isEmpty() && oldHighestBidPrice > 0) {
            refundFuture = CompletableFuture.runAsync(() -> {
                try {
                    auctionFeignClient.createMoneyWithMemberUuid(
                            CreateMoneyWithMemberUuidRequestDto.builder()
                                    .memberUuid(oldHighestMemberUuid)
                                    .amount(oldHighestBidPrice)
                                    .isPositive(true)
                                    .historyType(MoneyHistoryType.FREEZE)
                                    .moneyHistoryDetail("이전 입찰 보증금 반환")
                                    .build()
                    );
                    log.info("이전 입찰자 보증금 반환 완료: {}", oldHighestMemberUuid);
                } catch (Exception e) {
                    log.error("이전 입찰자 보증금 반환 실패: {}", oldHighestMemberUuid, e);
                    throw new RuntimeException("보증금 반환 실패", e);
                }
            });
        }
        
        // Step 2: 현재 입찰자 보증금 차감
        CompletableFuture<Void> chargeFuture = CompletableFuture.runAsync(() -> {
            try {
                auctionFeignClient.createMoneyWithMemberUuid(
                        CreateMoneyWithMemberUuidRequestDto.builder()
                                .memberUuid(updateAuctionDto.getMemberUuid())
                                .amount(updateAuctionDto.getBidPrice())
                                .isPositive(false)
                                .historyType(MoneyHistoryType.FREEZE)
                                .moneyHistoryDetail("입찰 보증금 차감")
                                .build()
                );
                log.info("현재 입찰자 보증금 차감 완료: {}", updateAuctionDto.getMemberUuid());
            } catch (Exception e) {
                log.error("현재 입찰자 보증금 차감 실패: {}", updateAuctionDto.getMemberUuid(), e);
                throw new RuntimeException("보증금 차감 실패", e);
            }
        });
        
        // Step 3: 모든 트랜잭션 완료 대기
        try {
            if (refundFuture != null) {
                refundFuture.get();
            }
            chargeFuture.get();
            log.info("Saga 트랜잭션 완료: {}", updateAuctionDto.getAuctionUuid());
            
        } catch (InterruptedException | ExecutionException e) {
            log.error("Saga 트랜잭션 실패: {}", updateAuctionDto.getAuctionUuid(), e);
            
            // 보상 트랜잭션 실행
            executeCompensationTransaction(updateAuctionDto, oldHighestMemberUuid, oldHighestBidPrice);
            
            throw new BaseException(BaseResponseStatus.MONEY_SERVICE_ERROR);
        }
    }
    
    /**
     * 보상 트랜잭션 실행
     */
    private void executeCompensationTransaction(UpdateAuctionDto updateAuctionDto,
                                              String oldHighestMemberUuid,
                                              Long oldHighestBidPrice) {
        log.warn("보상 트랜잭션 시작: {}", updateAuctionDto.getAuctionUuid());
        
        try {
            // 1. 현재 입찰자 보증금 반환 (차감 취소)
            if (updateAuctionDto.getMemberUuid() != null) {
                auctionFeignClient.createMoneyWithMemberUuid(
                        CreateMoneyWithMemberUuidRequestDto.builder()
                                .memberUuid(updateAuctionDto.getMemberUuid())
                                .amount(updateAuctionDto.getBidPrice())
                                .isPositive(true)
                                .historyType(MoneyHistoryType.FREEZE)
                                .moneyHistoryDetail("보상: 입찰 보증금 반환")
                                .build()
                );
                log.info("보상: 현재 입찰자 보증금 반환 완료");
            }
            
            // 2. 이전 입찰자 보증금 재차감 (반환 취소)
            if (oldHighestMemberUuid != null && !oldHighestMemberUuid.isEmpty() && oldHighestBidPrice > 0) {
                auctionFeignClient.createMoneyWithMemberUuid(
                        CreateMoneyWithMemberUuidRequestDto.builder()
                                .memberUuid(oldHighestMemberUuid)
                                .amount(oldHighestBidPrice)
                                .isPositive(false)
                                .historyType(MoneyHistoryType.FREEZE)
                                .moneyHistoryDetail("보상: 이전 입찰 보증금 재차감")
                                .build()
                );
                log.info("보상: 이전 입찰자 보증금 재차감 완료");
            }
            
            log.info("보상 트랜잭션 완료: {}", updateAuctionDto.getAuctionUuid());
            
        } catch (Exception e) {
            log.error("보상 트랜잭션 실패: {}", updateAuctionDto.getAuctionUuid(), e);
            // 보상 트랜잭션도 실패한 경우 수동 개입 필요
            throw new RuntimeException("보상 트랜잭션 실패", e);
        }
    }
    
    /**
     * 경매 종료 시 최종 정산 처리
     */
    @Transactional
    public void processAuctionCloseWithSaga(String auctionUuid, 
                                          String winnerMemberUuid, 
                                          Long finalBidPrice) {
        
        try {
            // 낙찰자 최종 결제 처리
            auctionFeignClient.createMoneyWithMemberUuid(
                    CreateMoneyWithMemberUuidRequestDto.builder()
                            .memberUuid(winnerMemberUuid)
                            .amount(finalBidPrice)
                            .isPositive(false)
                            .historyType(MoneyHistoryType.PRODUCT_BUY)
                            .moneyHistoryDetail("경매 낙찰 최종 결제")
                            .build()
            );
            
            log.info("경매 종료 Saga 완료: {}, 낙찰자: {}", auctionUuid, winnerMemberUuid);
            
        } catch (Exception e) {
            log.error("경매 종료 Saga 실패: {}", auctionUuid, e);
            throw new BaseException(BaseResponseStatus.MONEY_SERVICE_ERROR);
        }
    }
} 