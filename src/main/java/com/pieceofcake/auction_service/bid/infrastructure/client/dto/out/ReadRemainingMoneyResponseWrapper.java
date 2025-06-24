package com.pieceofcake.auction_service.bid.infrastructure.client.dto.out;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
public class ReadRemainingMoneyResponseWrapper {
    private String httpStatus;
    private boolean isSuccess;
    private String message;
    private int code;
    private ReadRemainingMoneyResponseDto result;
}
