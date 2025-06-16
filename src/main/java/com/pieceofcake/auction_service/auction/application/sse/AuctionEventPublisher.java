package com.pieceofcake.auction_service.auction.application.sse;

import com.pieceofcake.auction_service.auction.dto.out.UpdateAuctionPriceSseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic auctionPriceTopic;

    public void publishPriceUpdate(UpdateAuctionPriceSseDto event) {
        try {
            redisTemplate.convertAndSend(auctionPriceTopic.getTopic(), event);
            log.info("Published auction price update: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish auction price update", e);
        }
    }
}
