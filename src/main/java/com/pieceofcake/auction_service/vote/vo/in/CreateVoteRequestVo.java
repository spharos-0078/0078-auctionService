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
    @Schema(
            description = "경매 시작 시간",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2023-10-01T10:00:00"
    )
    private LocalDateTime startDate;
    @Schema(
            description = "경매 종료 시간",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2023-10-01T12:00:00"
    )
    private LocalDateTime endDate;

    @Builder
    public CreateVoteRequestVo(
            String pieceProductUuid,
            String productUuid,
            Long startingPrice,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        this.pieceProductUuid = pieceProductUuid;
        this.productUuid = productUuid;
        this.startingPrice = startingPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
