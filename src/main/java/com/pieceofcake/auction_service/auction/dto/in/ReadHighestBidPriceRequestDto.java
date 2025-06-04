package com.pieceofcake.auction_service.auction.dto.in;

import com.pieceofcake.auction_service.auction.vo.in.ReadHighestBidPriceRequestVo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadHighestBidPriceRequestDto {
    private String auctionUuid;

    @Builder
    public ReadHighestBidPriceRequestDto(String auctionUuid) {
        this.auctionUuid = auctionUuid;
    }

    public static ReadHighestBidPriceRequestDto from(ReadHighestBidPriceRequestVo readHighestBidPriceRequestVo) {
        return ReadHighestBidPriceRequestDto.builder()
                .auctionUuid(readHighestBidPriceRequestVo.getAuctionUuid())
                .build();
    }
}
