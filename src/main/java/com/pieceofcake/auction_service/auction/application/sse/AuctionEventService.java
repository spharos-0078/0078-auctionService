package com.pieceofcake.auction_service.auction.application.sse;

import com.pieceofcake.auction_service.auction.dto.out.UpdateAuctionPriceSseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionEventService implements MessageListener {

    // SSE 연결 관리자
    // 각 경매별로 연결 통로 (Sink) 생성/관리
    // Redis에서 메시지(갱신된 가격) 수신
    // 실시간 가격 정보 전송

    private final ChannelTopic auctionPriceTopic;
    private final RedisTemplate<String, Object> redisTemplate;
//    private final Jackson2JsonRedisSerializer<UpdateAuctionPriceSseDto> serializer; // 주입받기
    private final GenericJackson2JsonRedisSerializer serializer;

    private final StringRedisTemplate stringRedisTemplate;

    private final Map<String, Sinks.Many<UpdateAuctionPriceSseDto>> sinks = new ConcurrentHashMap<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 주입받은 serializer 사용
            Object obj = serializer.deserialize(message.getBody());

            if (obj instanceof UpdateAuctionPriceSseDto event) {
                String auctionUuid = event.getAuctionUuid();
                Sinks.Many<UpdateAuctionPriceSseDto> sink = sinks.get(auctionUuid);
                if (sink != null) {
                    sink.tryEmitNext(event);
                }
            }
        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }

    public Flux<UpdateAuctionPriceSseDto> getAuctionPriceUpdatesByAuctionId(String auctionUuid) {
        Sinks.Many<UpdateAuctionPriceSseDto> sink = sinks.computeIfAbsent(auctionUuid,
                k -> Sinks.many().multicast().onBackpressureBuffer());

        // 최초 접속 시 현재 최고가 정보도 전달
        retrieveAndSendCurrentHighestBid(auctionUuid, sink);

        return sink.asFlux();
    }

    private void retrieveAndSendCurrentHighestBid(String auctionUuid, Sinks.Many<UpdateAuctionPriceSseDto> sink) {
        try {
            String redisHighestBidKey = "auction:highestBid:" + auctionUuid;
            Map<Object, Object> bidData = stringRedisTemplate.opsForHash().entries(redisHighestBidKey);

            if (!bidData.isEmpty()) {
                UpdateAuctionPriceSseDto event = UpdateAuctionPriceSseDto.builder()
                        .auctionUuid(auctionUuid)
                        .bidPrice(Long.parseLong((String) bidData.get("bidPrice")))
                        .bidUuid((String) bidData.get("bidUuid"))
                        .memberUuid((String) bidData.get("bidMemberUuid"))
                        .build();

                sink.tryEmitNext(event);
            }
        } catch (Exception e) {
            log.error("Error retrieving current highest bid for auction: {}", auctionUuid, e);
        }
    }
}
