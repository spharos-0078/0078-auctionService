package com.pieceofcake.auction_service.auction.vo.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateAuctionVo {
    private String auctionUuid;  // 경매 UUID
    private Long bidPrice;
    private String memberUuid;

    @Builder
    public UpdateAuctionVo(
            String auctionUuid,
            Long bidPrice,
            String memberUuid,
            Long timestamp) {
        this.auctionUuid = auctionUuid;
        this.bidPrice = bidPrice;
        this.memberUuid = memberUuid;
    }
}
