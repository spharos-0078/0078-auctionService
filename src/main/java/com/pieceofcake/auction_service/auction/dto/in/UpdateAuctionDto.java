package com.pieceofcake.auction_service.auction.dto.in;

import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.vo.in.UpdateAuctionVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateAuctionDto {
    private String auctionUuid;  // 경매 UUID
    private String bidUuid;      // 입찰 UUID (최고 입찰의 UUID)
    private Long bidPrice;
    private String memberUuid;


    @Builder
    public UpdateAuctionDto(
            String auctionUuid,
            String bidUuid,
            Long bidPrice,
            String memberUuid
    ) {
        this.auctionUuid = auctionUuid;
        this.bidUuid = bidUuid;
        this.bidPrice = bidPrice;
        this.memberUuid = memberUuid;
    }

    public static UpdateAuctionDto of(
            String auctionUuid,
            String bidUuid,
            Long bidPrice,
            String memberUuid
    ) {
        return UpdateAuctionDto.builder()
                .auctionUuid(auctionUuid)
                .bidUuid(bidUuid)
                .bidPrice(bidPrice)
                .memberUuid(memberUuid)
                .build();
    }

    public Auction updateEntity(Auction auction) {
        return Auction.builder()
                .id(auction.getId())
                .auctionUuid(auction.getAuctionUuid())
                .productUuid(auction.getProductUuid())
                .startingPrice(auction.getStartingPrice())
                .highestBidUuid(this.bidUuid)
                .highestBidPrice(this.bidPrice)
                .highestBidMemberUuid(this.memberUuid)
                .startDate(auction.getStartDate())
                .endDate(auction.getEndDate())
                .auctionStatus(auction.getAuctionStatus())
                .build();
    }

    public UpdateAuctionVo toVo() {
        return UpdateAuctionVo.builder()
                .auctionUuid(this.auctionUuid)
                .bidPrice(this.bidPrice)
                .memberUuid(this.memberUuid)
                .build();
    }
}
