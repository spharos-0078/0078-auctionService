package com.pieceofcake.auction_service.vote.vo.in;

import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateVoteRequestVo {

    @Schema(
            description = "상품 UUID",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "product-123"
    )
    private String productUuid;
    @Schema(
            description = "조각상품 UUID",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "piece-product-123"
    )
    private String pieceProductUuid;
    @Schema(
            description = "경매 열릴 시 시작 가격",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "10000"
    )
    private Long startingPrice;

    @Builder
    public CreateVoteRequestVo(
            String pieceProductUuid,
            String productUuid,
            Long startingPrice
    ) {
        this.pieceProductUuid = pieceProductUuid;
        this.productUuid = productUuid;
        this.startingPrice = startingPrice;
    }
}
