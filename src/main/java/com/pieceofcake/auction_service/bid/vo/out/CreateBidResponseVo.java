package com.pieceofcake.auction_service.bid.vo.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateBidResponseVo {
    @Schema(
            description = "입찰 생성 성공 여부",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean success;
    @Schema(
            description = "입찰 생성 결과 메시지",
            example = "{입찰 성공, 경매 마감, 입찰 실패, 예치금 부족}",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;

    @Builder
    public CreateBidResponseVo(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
