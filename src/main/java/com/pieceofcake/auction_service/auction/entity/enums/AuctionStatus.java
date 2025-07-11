package com.pieceofcake.auction_service.auction.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionStatus {
    READY("준비중"),
    ONGOING("진행중"),
    CLOSED("종료됨"),
    CANCELLED("취소됨"),
    NO_BID("입찰 없음");

    @JsonValue
    private final String label;
}
