package com.pieceofcake.auction_service.auction.dto.out;

import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.vo.out.ReadAuctionListResponseVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadAuctionListResponseDto {
    private String auctionUuid;
    private String productUuid;
    private String pieceProductUuid;

    @Builder
    public ReadAuctionListResponseDto(
            String auctionUuid,
            String productUuid,
            String pieceProductUuid
    ) {
        this.auctionUuid = auctionUuid;
        this.productUuid = productUuid;
        this.pieceProductUuid = pieceProductUuid;
    }

    public static ReadAuctionListResponseDto from(Auction auction) {
        return ReadAuctionListResponseDto.builder()
                .auctionUuid(auction.getAuctionUuid())
                .productUuid(auction.getProductUuid())
                .pieceProductUuid(auction.getPieceProductUuid())
                .build();
    }

    public ReadAuctionListResponseVo toVo() {
        return ReadAuctionListResponseVo.builder()
                .auctionUuid(auctionUuid)
                .productUuid(productUuid)
                .pieceProductUuid(pieceProductUuid)
                .build();
    }
}
