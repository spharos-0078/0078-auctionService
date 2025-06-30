package com.pieceofcake.auction_service.auction.vo.in;

import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateAuctionRequestVo {
    @Schema(
            description = "경매 상품 UUID",
            example = "10000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String productUuid;
    @Schema(
            description = "조각 상품 UUID",
            example = "piece-123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String pieceProductUuid;
    @Schema(
            description = "경매 시작 가격",
            example = "10000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long startingPrice;
    @Schema(
            description = "최고 입찰 uuid",
            example = "bid-123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String highestBidUuid;
    @Schema(
            description = "최고 입찰가. 경매가 진행됨에 따라 update 될 예정",
            example = "123000"
    )
    private Long highestBidPrice;
    @Schema(
            description = "최고 입찰자 UUID. 경매가 진행됨에 따라 update 될 예정",
            example = "member-123"
    )
    private String highestBidMemberUuid;
    @Schema(
            description = "경매 시작일",
            example = "2025-06-24T00:00:000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime startDate;
    @Schema(
            description = "경매 종료일",
            example = "2025-06-30T00:00:000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime endDate;
}
