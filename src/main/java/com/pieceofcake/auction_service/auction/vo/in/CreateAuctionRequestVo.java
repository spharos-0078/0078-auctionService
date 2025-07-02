package com.pieceofcake.auction_service.auction.vo.in;

import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
public class CreateAuctionRequestVo {
    @Schema(
            description = "경매 상품 UUID",
            example = "10000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "상품 UUID는 필수입니다")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "올바른 UUID 형식이 아닙니다")
    private String productUuid;
    @Schema(
            description = "조각 상품 UUID",
            example = "piece-123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "조각 상품 UUID는 필수입니다")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "올바른 UUID 형식이 아닙니다")
    private String pieceProductUuid;
    @Schema(
            description = "경매 시작 가격",
            example = "10000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "시작 가격은 필수입니다")
    @Min(value = 1, message = "시작 가격은 1원 이상이어야 합니다")
    @Max(value = 999999999999L, message = "시작 가격은 999,999,999,999원 이하여야 합니다")
    private Long startingPrice;
    @Schema(
            description = "최고 입찰 uuid",
            example = "bid-123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "올바른 UUID 형식이 아닙니다")
    private String highestBidUuid;
    @Schema(
            description = "최고 입찰가. 경매가 진행됨에 따라 update 될 예정",
            example = "123000"
    )
    @Min(value = 0, message = "최고 입찰가는 0원 이상이어야 합니다")
    @Max(value = 999999999999L, message = "최고 입찰가는 999,999,999,999원 이하여야 합니다")
    private Long highestBidPrice;
    @Schema(
            description = "최고 입찰자 UUID. 경매가 진행됨에 따라 update 될 예정",
            example = "member-123"
    )
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "올바른 UUID 형식이 아닙니다")
    private String highestBidMemberUuid;
    @Schema(
            description = "경매 시작일",
            example = "2025-06-24T00:00:000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "시작일은 필수입니다")
    @Future(message = "시작일은 현재 시간 이후여야 합니다")
    private LocalDateTime startDate;
    @Schema(
            description = "경매 종료일",
            example = "2025-06-30T00:00:000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "종료일은 필수입니다")
    @Future(message = "종료일은 현재 시간 이후여야 합니다")
    private LocalDateTime endDate;
    @Schema(
            description = "경매 상태",
            example = "ONGOING",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private AuctionStatus auctionStatus;
}
