package com.pieceofcake.auction_service.bid.infrastructure.client.dto.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadRemainingMoneyResponseDto {
    private Long remainingMoney;

    @Builder
    public ReadRemainingMoneyResponseDto(Long remainingMoney) {
        this.remainingMoney = remainingMoney;
    }
}
