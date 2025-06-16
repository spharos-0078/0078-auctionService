package com.pieceofcake.auction_service.auction.dto.out;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class UpdateAuctionPriceSseDto {
    private String auctionUuid;
    private Long bidPrice;
    private String bidUuid;
    private String memberUuid;

    @Builder
    public UpdateAuctionPriceSseDto(
            String auctionUuid,
            Long bidPrice,
            String bidUuid,
            String memberUuid
    ) {
        this.auctionUuid = auctionUuid;
        this.bidPrice = bidPrice;
        this.bidUuid = bidUuid;
        this.memberUuid = memberUuid;
    }
}
