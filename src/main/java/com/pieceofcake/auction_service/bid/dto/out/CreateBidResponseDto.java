package com.pieceofcake.auction_service.bid.dto.out;

import com.pieceofcake.auction_service.bid.vo.out.CreateBidResponseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateBidResponseDto {
    public Boolean success;
    public String message;

    @Builder
    public CreateBidResponseDto(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public CreateBidResponseVo toVo() {
        return CreateBidResponseVo.builder()
                .success(success)
                .message(message)
                .build();
    }
}
