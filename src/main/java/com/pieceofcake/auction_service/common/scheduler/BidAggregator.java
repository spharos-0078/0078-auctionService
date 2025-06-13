package com.pieceofcake.auction_service.common.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 역할: 매 1초마다 Redis에 누적된 입찰을 집계하여 DB에 반영 (최고가 업데이트 등)
@Component
@RequiredArgsConstructor
public class BidAggregator {

}
