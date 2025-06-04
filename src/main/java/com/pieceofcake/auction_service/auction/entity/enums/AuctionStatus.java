package com.pieceofcake.auction_service.auction.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionStatus {
    ONGOING("진행중"),
    COMPLETED("종료됨"),
    CANCELLED("취소됨");

    @JsonValue
    private final String label;
}
