package com.pieceofcake.auction_service.vote.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VoteStatus {
    READY("투표 준비중"),
    OPEN("투표 가능"),
    CLOSED_ACCEPTED("투표 종료(찬성)"),
    CLOSED_REJECTED("투표 종료(거절)"),
    DELETED("투표 삭제됨");

    private final String label;
}
