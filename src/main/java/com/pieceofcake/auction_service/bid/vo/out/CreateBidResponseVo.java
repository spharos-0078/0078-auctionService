package com.pieceofcake.auction_service.bid.vo.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateBidResponseVo {
    private Boolean success;
    private String message;

    @Builder
    public CreateBidResponseVo(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
