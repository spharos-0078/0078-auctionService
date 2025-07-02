package com.pieceofcake.auction_service.auction.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionSchedulerService {
    
    private final AuctionService auctionService;
    
    /**
     * 스케줄러에서 호출되는 경매 종료 메서드
     * 별도 트랜잭션으로 실행하여 스케줄러 컨텍스트와 분리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void closeAuctionWithNewTransaction(String auctionUuid) {
        try {
            log.info("스케줄러에서 경매 종료 시작: {}", auctionUuid);
            auctionService.closeAuction(auctionUuid);
            log.info("스케줄러에서 경매 종료 완료: {}", auctionUuid);
        } catch (Exception e) {
            log.error("스케줄러에서 경매 종료 실패: {}", auctionUuid, e);
            // 스케줄러 실패는 로그만 남기고 예외를 던지지 않음
            // 재시도 로직은 별도로 구현 필요
        }
    }
    
    /**
     * 경매 종료 재시도 로직
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void retryCloseAuction(String auctionUuid, int retryCount) {
        if (retryCount > 3) {
            log.error("경매 종료 재시도 횟수 초과: {}, retryCount: {}", auctionUuid, retryCount);
            return;
        }
        
        try {
            log.info("경매 종료 재시도: {}, retryCount: {}", auctionUuid, retryCount);
            auctionService.closeAuction(auctionUuid);
            log.info("경매 종료 재시도 성공: {}", auctionUuid);
        } catch (Exception e) {
            log.error("경매 종료 재시도 실패: {}, retryCount: {}", auctionUuid, retryCount, e);
            // 재귀적으로 재시도
            retryCloseAuction(auctionUuid, retryCount + 1);
        }
    }
} 