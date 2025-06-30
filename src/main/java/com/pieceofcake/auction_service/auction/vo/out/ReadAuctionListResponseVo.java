package com.pieceofcake.auction_service.auction.vo.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadAuctionListResponseVo {
    private String auctionUuid;
    private String productUuid;
    private String pieceProductUuid;

    @Builder
    public ReadAuctionListResponseVo(
            String auctionUuid,
            String productUuid,
            String pieceProductUuid
    ) {
        this.auctionUuid = auctionUuid;
        this.productUuid = productUuid;
        this.pieceProductUuid = pieceProductUuid;
    }
}
