package com.pieceofcake.auction_service.vote.infrastructure.client.dto.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPieceRequestDto {
    private String productUuid;

    @Builder
    public MemberPieceRequestDto(String productUuid) {
        this.productUuid = productUuid;
    }

    public static MemberPieceRequestDto from(String productUuid) {
        return MemberPieceRequestDto.builder()
                .productUuid(productUuid)
                .build();
    }
}
