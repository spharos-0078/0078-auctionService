package com.pieceofcake.auction_service.bid.application;

import com.pieceofcake.auction_service.bid.dto.in.CreateBidRequestDto;
import com.pieceofcake.auction_service.bid.entity.Bid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtomicBidService {
    
    private final StringRedisTemplate redisTemplate;
    
    // Redis Lua Script for atomic bid processing
    private static final String ATOMIC_BID_SCRIPT = """
        local auctionKey = KEYS[1]
        local newBidPrice = tonumber(ARGV[1])
        local newBidUuid = ARGV[2]
        local newMemberUuid = ARGV[3]
        local newTimestamp = ARGV[4]
        
        -- Get current highest bid price
        local currentPrice = redis.call('HGET', auctionKey, 'bidPrice')
        local currentPriceNum = currentPrice and tonumber(currentPrice) or 0
        
        -- Check if new bid is higher
        if newBidPrice > currentPriceNum then
            -- Atomically update the highest bid
            redis.call('HSET', auctionKey, 
                'bidPrice', tostring(newBidPrice),
                'bidUuid', newBidUuid,
                'bidMemberUuid', newMemberUuid,
                'timestamp', newTimestamp
            )
            -- Set expiration (14 days)
            redis.call('EXPIRE', auctionKey, 1209600)
            return 1  -- Success
        else
            return 0  -- Bid too low
        end
    """;
    
    private static final String BID_LOCK_SCRIPT = """
        local lockKey = KEYS[1]
        local lockValue = ARGV[1]
        local lockTime = tonumber(ARGV[2])
        
        -- Try to acquire lock
        local result = redis.call('SET', lockKey, lockValue, 'NX', 'EX', lockTime)
        if result then
            return 1  -- Lock acquired
        else
            return 0  -- Lock failed
        end
    """;
    
    private static final String BID_UNLOCK_SCRIPT = """
        local lockKey = KEYS[1]
        local lockValue = ARGV[1]
        
        -- Release lock only if it's ours
        if redis.call('GET', lockKey) == lockValue then
            return redis.call('DEL', lockKey)
        else
            return 0
        end
    """;
    
    /**
     * 원자적으로 입찰을 처리합니다.
     * @param auctionUuid 경매 UUID
     * @param bid 입찰 정보
     * @return true if bid was successful (highest bid), false otherwise
     */
    public boolean processBidAtomically(String auctionUuid, Bid bid) {
        String auctionKey = "auction:highestBid:" + auctionUuid;
        String lockKey = "auction:lock:" + auctionUuid;
        String lockValue = bid.getBidUuid() + ":" + System.currentTimeMillis();
        
        try {
            // 1. Try to acquire lock
            Boolean lockAcquired = redisTemplate.execute(
                new DefaultRedisScript<>(BID_LOCK_SCRIPT, Boolean.class),
                List.of(lockKey),
                lockValue,
                "5"  // 5 seconds lock
            );
            
            if (Boolean.FALSE.equals(lockAcquired)) {
                log.warn("Failed to acquire lock for auction: {}", auctionUuid);
                return false;
            }
            
            // 2. Process bid atomically
            Long result = redisTemplate.execute(
                new DefaultRedisScript<>(ATOMIC_BID_SCRIPT, Long.class),
                List.of(auctionKey),
                String.valueOf(bid.getBidPrice()),
                bid.getBidUuid(),
                bid.getMemberUuid(),
                String.valueOf(System.currentTimeMillis())
            );
            
            return result != null && result == 1;
            
        } finally {
            // 3. Always release lock
            redisTemplate.execute(
                new DefaultRedisScript<>(BID_UNLOCK_SCRIPT, Long.class),
                List.of(lockKey),
                lockValue
            );
        }
    }
    
    /**
     * 현재 최고가를 안전하게 조회합니다.
     */
    public BidInfo getCurrentHighestBid(String auctionUuid) {
        String auctionKey = "auction:highestBid:" + auctionUuid;
        var entries = redisTemplate.opsForHash().entries(auctionKey);
        
        if (entries.isEmpty()) {
            return new BidInfo(0L, null, null, 0L);
        }
        
        Long bidPrice = entries.get("bidPrice") != null 
            ? Long.parseLong((String) entries.get("bidPrice")) 
            : 0L;
        String bidUuid = (String) entries.get("bidUuid");
        String memberUuid = (String) entries.get("bidMemberUuid");
        Long timestamp = entries.get("timestamp") != null 
            ? Long.parseLong((String) entries.get("timestamp")) 
            : 0L;
            
        return new BidInfo(bidPrice, bidUuid, memberUuid, timestamp);
    }
    
    public static class BidInfo {
        private final Long bidPrice;
        private final String bidUuid;
        private final String memberUuid;
        private final Long timestamp;
        
        public BidInfo(Long bidPrice, String bidUuid, String memberUuid, Long timestamp) {
            this.bidPrice = bidPrice;
            this.bidUuid = bidUuid;
            this.memberUuid = memberUuid;
            this.timestamp = timestamp;
        }
        
        // Getters
        public Long getBidPrice() { return bidPrice; }
        public String getBidUuid() { return bidUuid; }
        public String getMemberUuid() { return memberUuid; }
        public Long getTimestamp() { return timestamp; }
    }
} 