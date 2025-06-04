package com.pieceofcake.auction_service.bid.vo.in;

import lombok.Getter;

@Getter
public class CreateBidRequestVo {
    private String auctionUuid;
    private Long bidPrice;

    public CreateBidRequestVo(String auctionUuid, Long bidPrice) {
        this.auctionUuid = auctionUuid;
        this.bidPrice = bidPrice;
    }
}
