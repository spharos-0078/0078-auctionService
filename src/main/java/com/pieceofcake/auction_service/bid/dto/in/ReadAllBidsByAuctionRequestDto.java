package com.pieceofcake.auction_service.bid.dto.in;

import com.pieceofcake.auction_service.bid.vo.in.ReadAllBidsByAuctionRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadAllBidsByAuctionRequestDto {
    private String auctionUuid;

    @Builder
    public ReadAllBidsByAuctionRequestDto(String auctionUuid) {
        this.auctionUuid = auctionUuid;
    }

    public static ReadAllBidsByAuctionRequestDto from(ReadAllBidsByAuctionRequestVo readAllBidsByAuctionRequestVo) {
        return new ReadAllBidsByAuctionRequestDto(
                readAllBidsByAuctionRequestVo.getAuctionUuid()
        );
    }
}
