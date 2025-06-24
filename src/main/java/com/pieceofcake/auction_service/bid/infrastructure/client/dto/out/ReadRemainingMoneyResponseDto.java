package com.pieceofcake.auction_service.bid.infrastructure.client.dto.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class ReadRemainingMoneyResponseDto {
    private Long amount;

    @Builder
    public ReadRemainingMoneyResponseDto(Long amount) {
        this.amount = amount;
    }
}
