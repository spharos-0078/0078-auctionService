package com.pieceofcake.auction_service.bid.dto.in;

import com.pieceofcake.auction_service.bid.entity.Bid;
import com.pieceofcake.auction_service.bid.vo.in.CreateBidRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateBidRequestDto {
    private String memberUuid;
    private String auctionUuid;
    private Long bidPrice;

    @Builder
    public CreateBidRequestDto(String memberUuid, String auctionUuid, Long bidPrice) {
        this.memberUuid = memberUuid;
        this.auctionUuid = auctionUuid;
        this.bidPrice = bidPrice;
    }

    public static CreateBidRequestDto of(String memberUuid, CreateBidRequestVo createBidRequestVo) {
        return CreateBidRequestDto.builder()
                .memberUuid(memberUuid)
                .auctionUuid(createBidRequestVo.getAuctionUuid())
                .bidPrice(createBidRequestVo.getBidPrice())
                .build();
    }

    public Bid toEntity() {
        return Bid.builder()
                .bidUuid(UUID.randomUUID().toString())
                .auctionUuid(auctionUuid)
                .memberUuid(memberUuid)
                .bidPrice(bidPrice)
                .isHighestBid(true)
                .isHidden(false)
                .build();
    }
}
