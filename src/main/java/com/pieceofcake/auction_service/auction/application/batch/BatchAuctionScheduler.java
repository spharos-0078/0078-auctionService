package com.pieceofcake.auction_service.auction.application.batch;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Set<String> batchKeys = redisTemplate.keys("auction:useScheduler:*");
        if (batchKeys == null) return;

        for (String batchKey : batchKeys) {
            String auctionUuid = batchKey.replace("auction:useScheduler:", "");

            String redisHighestBidKey = "auction:highestBid:" + auctionUuid;
            Map<Object, Object> redisHighestBidMap = redisTemplate.opsForHash().entries(redisHighestBidKey);

            if (redisHighestBidMap.isEmpty()) {
                log.info("BatchAuctionScheduler: No highest bid found for auctionUuid: {}", auctionUuid);
            };

            Long bidPrice = redisHighestBidMap.get("bidPrice") != null
                    ? Long.parseLong((String) redisHighestBidMap.get("bidPrice"))
                    : 0L;
            String bidUuid = (String) redisHighestBidMap.get("bidUuid");
            String bidMemberUuid = (String) redisHighestBidMap.get("bidMemberUuid");

            auctionService.updateAuction(UpdateAuctionDto.builder()
                    .auctionUuid(auctionUuid)
                    .bidUuid(bidUuid)
                    .bidPrice(bidPrice)
                    .memberUuid(bidMemberUuid)
                    .build());

            // 처리 완료된 flag 삭제
            redisTemplate.delete(batchKey);

        }
    }
}
