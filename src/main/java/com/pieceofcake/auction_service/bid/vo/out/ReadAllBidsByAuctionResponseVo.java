package com.pieceofcake.auction_service.bid.vo.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadAllBidsByAuctionResponseVo {

    private String bidUuid;
    private String bidPrice;
    private String memberUuid;

    @Builder
    public ReadAllBidsByAuctionResponseVo(String bidUuid, String bidPrice, String memberUuid) {
        this.bidUuid = bidUuid;
        this.bidPrice = bidPrice;
        this.memberUuid = memberUuid;
    }
}
