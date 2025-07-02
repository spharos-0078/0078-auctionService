package com.pieceofcake.auction_service.auction.application.scheduler;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchAuctionScheduler {
    private final AuctionService auctionService;
    private final StringRedisTemplate redisTemplate;

    @Scheduled(fixedDelay = 1000)
    public void updateAuction() {
        try {
            // Redis 키 스캔 최적화 - 패턴 매칭 사용
            Set<String> batchKeys = redisTemplate.keys("auction:useScheduler:*");
            if (batchKeys == null || batchKeys.isEmpty()) return;

            log.debug("배치 처리 대상 경매 수: {}", batchKeys.size());

            for (String batchKey : batchKeys) {
                try {
                    String auctionUuid = batchKey.replace("auction:useScheduler:", "");
                    processAuctionUpdate(auctionUuid);
                } catch (Exception e) {
                    log.error("배치 처리 중 오류 발생 - auctionKey: {}", batchKey, e);
                    // 개별 경매 처리 실패는 전체 배치에 영향을 주지 않도록 계속 진행
                }
            }
        } catch (Exception e) {
            log.error("배치 처리 전체 실패", e);
        }
    }
    
    private void processAuctionUpdate(String auctionUuid) {
        String redisHighestBidKey = "auction:highestBid:" + auctionUuid;
        Map<Object, Object> redisHighestBidMap = redisTemplate.opsForHash().entries(redisHighestBidKey);

        if (redisHighestBidMap.isEmpty()) {
            log.debug("BatchAuctionScheduler: No highest bid found for auctionUuid: {}", auctionUuid);
            return;
        }

        Long bidPrice = redisHighestBidMap.get("bidPrice") != null
                ? Long.parseLong((String) redisHighestBidMap.get("bidPrice"))
                : 0L;
        String bidUuid = (String) redisHighestBidMap.get("bidUuid");
        String bidMemberUuid = (String) redisHighestBidMap.get("bidMemberUuid");
        
        if (bidPrice <= 0 || bidUuid == null || bidMemberUuid == null) {
            log.warn("유효하지 않은 입찰 데이터: auctionUuid: {}, bidPrice: {}, bidUuid: {}, bidMemberUuid: {}", 
                    auctionUuid, bidPrice, bidUuid, bidMemberUuid);
            return;
        }
        
        log.debug("배치에서 updateAuction 호출 - auctionUuid: {}, bidUuid: {}, bidPrice: {}, bidMemberUuid: {}",
                auctionUuid, bidUuid, bidPrice, bidMemberUuid);
                
        auctionService.updateAuction(UpdateAuctionDto.builder()
                .auctionUuid(auctionUuid)
                .bidUuid(bidUuid)
                .bidPrice(bidPrice)
                .memberUuid(bidMemberUuid)
                .build());
                
        // 처리 완료 후 스케줄러 플래그 제거
        redisTemplate.delete("auction:useScheduler:" + auctionUuid);
    }
}
