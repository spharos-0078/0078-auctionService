package com.pieceofcake.auction_service.common.sse;

import com.pieceofcake.auction_service.auction.dto.out.UpdateAuctionDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

// 역할: 경매별 SSE Flux 스트림 생성 및 이벤트 발행 관리
@Service
public class SseEmitterService {
    // 경매ID별 Sinks 저장 맵
    private final ConcurrentHashMap<String, Sinks.Many<UpdateAuctionDto>> sinksMap = new ConcurrentHashMap<>();

    // 경매 구독 시 Flux 제공
    public Flux<UpdateAuctionDto> createFluxForAuction(String auctionUuid) {
        // 해당 경매에 대한 Sink가 이미 있으면 재사용, 없으면 생성
        Sinks.Many<UpdateAuctionDto> sink = sinksMap.computeIfAbsent(auctionUuid, key ->
                Sinks.many().multicast().onBackpressureBuffer());
        // 새 구독 Flux 리턴 (완료/에러 신호 시 현재 Flux subscriber 제거)
        return sink.asFlux();
    }

    // 새로운 입찰 최고가 업데이트 발생 시 호출되어 이벤트 푸시
    public void pushUpdate(UpdateAuctionDto updateAuctionDto) {
        Sinks.Many<UpdateAuctionDto> sink = sinksMap.get(updateAuctionDto.getAuctionUuid());
        if (sink != null) {
            sink.tryEmitNext(updateAuctionDto);
        }
    }
}