package com.pieceofcake.auction_service.bid.vo.in;

import lombok.Getter;
import jakarta.validation.constraints.*;

@Getter
public class CreateBidRequestVo {
    @NotBlank(message = "경매 UUID는 필수입니다")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "올바른 UUID 형식이 아닙니다")
    private String auctionUuid;
    
    @NotNull(message = "입찰가는 필수입니다")
    @Min(value = 1, message = "입찰가는 1원 이상이어야 합니다")
    @Max(value = 999999999999L, message = "입찰가는 999,999,999,999원 이하여야 합니다")
    private Long bidPrice;

    public CreateBidRequestVo(String auctionUuid, Long bidPrice) {
        this.auctionUuid = auctionUuid;
        this.bidPrice = bidPrice;
    }
}
