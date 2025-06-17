package com.pieceofcake.auction_service.vote.infrastructure.client.dto.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPieceResponseDto {
    private String memberUuid;
    private int quantity;

    @Builder
    public MemberPieceResponseDto(String memberUuid, int quantity) {
        this.memberUuid = memberUuid;
        this.quantity = quantity;
    }
}
