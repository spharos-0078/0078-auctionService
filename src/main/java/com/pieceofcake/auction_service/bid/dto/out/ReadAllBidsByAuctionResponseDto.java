package com.pieceofcake.auction_service.bid.dto.out;

import com.pieceofcake.auction_service.bid.entity.Bid;
import com.pieceofcake.auction_service.bid.vo.out.ReadAllBidsByAuctionResponseVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadAllBidsByAuctionResponseDto {
    private String bidUuid;
    private String bidPrice;
    private String memberUuid;

    @Builder
    public ReadAllBidsByAuctionResponseDto(String bidUuid, String bidPrice, String memberUuid) {
        this.bidUuid = bidUuid;
        this.bidPrice = bidPrice;
        this.memberUuid = memberUuid;
    }

    public static ReadAllBidsByAuctionResponseDto from(Bid bid) {
        return ReadAllBidsByAuctionResponseDto.builder()
                .bidUuid(bid.getBidUuid())
                .bidPrice(bid.getBidPrice().toString())
                .memberUuid(bid.getMemberUuid())
                .build();
    }

    public ReadAllBidsByAuctionResponseVo toVo() {
        return ReadAllBidsByAuctionResponseVo.builder()
                .bidUuid(this.bidUuid)
                .bidPrice(this.bidPrice)
                .memberUuid(this.memberUuid)
                .build();
    }
}
