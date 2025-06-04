package com.pieceofcake.auction_service.auction.vo.out;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadHighestBidPriceResponseVo {
    private final Long highestBidPrice;

    @Builder
    public ReadHighestBidPriceResponseVo(Long highestBidPrice) {
        this.highestBidPrice = highestBidPrice;
    }
}
