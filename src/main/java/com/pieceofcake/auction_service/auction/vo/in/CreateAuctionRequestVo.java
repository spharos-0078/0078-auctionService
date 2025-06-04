package com.pieceofcake.auction_service.auction.vo.in;

import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateAuctionRequestVo {
    private String productUuid;
    private Long startingPrice;
    private Long highestBidPrice;
    private String highestBidMemberUuid;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private AuctionStatus auctionStatus;
}
