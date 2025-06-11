package com.pieceofcake.auction_service.common.scheduler;

import com.pieceofcake.auction_service.auction.dto.out.UpdateAuctionDto;
import com.pieceofcake.auction_service.auction.infrastructure.AuctionRepository;
import com.pieceofcake.auction_service.common.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;
import java.util.Set;

// 역할: 매 1초마다 Redis에 누적된 입찰을 집계하여 DB에 반영 (최고가 업데이트 등)
@Component
@RequiredArgsConstructor
public class BidAggregator {

}
