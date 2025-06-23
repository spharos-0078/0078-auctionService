package com.pieceofcake.auction_service.vote.infrastructure.client.dto.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPieceResponseDto {
    private String memberUuid;
    private int pieceQuantity;

    @Builder
    public MemberPieceResponseDto(String memberUuid, int pieceQuantity) {
        this.memberUuid = memberUuid;
        this.pieceQuantity = pieceQuantity;
    }
}
