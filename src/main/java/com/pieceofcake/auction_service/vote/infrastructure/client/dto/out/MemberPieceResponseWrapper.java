package com.pieceofcake.auction_service.vote.infrastructure.client.dto.out;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
@ToString
@Data
@NoArgsConstructor
public class MemberPieceResponseWrapper {
    private String httpStatus;
    private boolean isSuccess;
    private String message;
    private int code;
    private List<MemberPieceResponseDto> result;
}
