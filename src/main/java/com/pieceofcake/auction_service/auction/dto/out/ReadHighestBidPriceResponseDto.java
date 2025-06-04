package com.pieceofcake.auction_service.auction.dto.out;

import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.vo.out.ReadHighestBidPriceResponseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadHighestBidPriceResponseDto {
    private Long highestBidPrice;

    @Builder
    public ReadHighestBidPriceResponseDto(Long highestBidPrice) {
        this.highestBidPrice = highestBidPrice;
    }

    public static ReadHighestBidPriceResponseDto from(Auction auction) {
        return ReadHighestBidPriceResponseDto.builder()
                .highestBidPrice(auction.getHighestBidPrice())
                .build();
    }

    public ReadHighestBidPriceResponseVo toVo() {
        return ReadHighestBidPriceResponseVo.builder()
                .highestBidPrice(highestBidPrice)
                .build();
    }
}
