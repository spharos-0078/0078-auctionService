package com.pieceofcake.auction_service.vote.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VoteChoice {
    AGREE("찬성"),
    DISAGREE("반대"),
    ABSTAIN("기권"),
    NOT_VOTED("투표하지 않음");

    private final String label;
}
