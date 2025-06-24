package com.pieceofcake.auction_service.auction.infrastructure.client.dto.out;

import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateMoneyResponseWrapper {
    private String httpStatus;
    private boolean isSuccess;
    private String message;
    private int code;
    private Object result;
}
