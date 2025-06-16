package com.pieceofcake.auction_service.bid.dto.in;

import com.pieceofcake.auction_service.bid.entity.Bid;
import com.pieceofcake.auction_service.bid.vo.in.HideBidRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HideBidRequestDto {
    private String bidUuid;
    private String memberUuid;

    @Builder
    public HideBidRequestDto(String bidUuid, String memberUuid) {
        this.bidUuid = bidUuid;
        this.memberUuid = memberUuid;
    }

    public static HideBidRequestDto from(HideBidRequestVo hideBidRequestVo) {
        return HideBidRequestDto.builder()
                .bidUuid(hideBidRequestVo.getBidUuid())
                .memberUuid(hideBidRequestVo.getMemberUuid())
                .build();
    }

    public Bid udpateEntity(Bid bid) {
        return Bid.builder()
                .id(bid.getId())
                .bidUuid(bid.getBidUuid())
                .auctionUuid(bid.getAuctionUuid())
                .memberUuid(bid.getMemberUuid())
                .bidPrice(bid.getBidPrice())
                .isHighestBid(bid.getIsHighestBid())
                .hidden(true)
                .build();
    }
}
