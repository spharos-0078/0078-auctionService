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
            description = "경매 요청한 사람 UUID. 투표 시작 시 이 사람의 보증금이 묶이고, 경매 시작 시 반드시 이 사람이 입찰하게 됨",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "10000"
    )
    private String startingMemberUuid;
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
            String productUuid,
            Long startingPrice,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        this.productUuid = productUuid;
        this.startingPrice = startingPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
