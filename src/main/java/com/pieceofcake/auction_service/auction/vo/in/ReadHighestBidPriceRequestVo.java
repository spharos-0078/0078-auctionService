package com.pieceofcake.auction_service.auction.vo.in;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadHighestBidPriceRequestVo {
    private String auctionUuid;

    @Builder
    public ReadHighestBidPriceRequestVo(String auctionUuid) {
        this.auctionUuid = auctionUuid;
    }
}
