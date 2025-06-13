package com.pieceofcake.auction_service.bid.vo.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadMyAuctionsResponseVo {
    private String auctionUuid;

    @Builder
    public ReadMyAuctionsResponseVo(String auctionUuid) {
        this.auctionUuid = auctionUuid;
    }
}
