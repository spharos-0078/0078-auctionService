package com.pieceofcake.auction_service.bid.vo.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadBidRequestVo {
    private String auctionUuid;
}
