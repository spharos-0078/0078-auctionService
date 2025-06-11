package com.pieceofcake.auction_service.auction.vo.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateAuctionVo {
    private String auctionUuid;  // 경매 UUID
    private Long bidPrice;
    private String memberUuid;
    private Long timestamp;

    @Builder
    public UpdateAuctionVo(
            String auctionUuid,
            Long bidPrice,
            String memberUuid,
            Long timestamp) {
        this.auctionUuid = auctionUuid;
        this.bidPrice = bidPrice;
        this.memberUuid = memberUuid;
        this.timestamp = timestamp;
    }
}
