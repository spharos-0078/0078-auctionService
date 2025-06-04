package com.pieceofcake.auction_service.bid.vo.in;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadAllBidsByAuctionRequestVo {
    private String auctionUuid;

    @Builder
    public ReadAllBidsByAuctionRequestVo(String auctionUuid) {
        this.auctionUuid = auctionUuid;
    }
}
