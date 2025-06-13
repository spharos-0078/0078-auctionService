package com.pieceofcake.auction_service.bid.dto.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadMyAuctionsRequestDto {
    private String memberUuid;

    @Builder
    public ReadMyAuctionsRequestDto(String memberUuid) {
        this.memberUuid = memberUuid;
    }

    public static ReadMyAuctionsRequestDto of(String memberUuid) {
        return ReadMyAuctionsRequestDto.builder()
                .memberUuid(memberUuid)
                .build();
    }
}
