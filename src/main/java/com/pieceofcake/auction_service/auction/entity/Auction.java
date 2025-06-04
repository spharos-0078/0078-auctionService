package com.pieceofcake.auction_service.auction.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "auction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "auction_uuid", nullable = false)
    private String auctionUuid;

    @Column(name = "productUuid", nullable = false)
    private String productUuid;

    @Column(name = "starting_price", nullable = false)
    private Long startingPrice;

    @Column(name = "highest_bid_price", nullable = false)
    private Long highestBidPrice;

    @Column(name = "highest_bid_member_uuid", nullable = true)
    private String highestBidMemberUuid;

    @Column(name = "start_Date", nullable = false)
    private String startDate;

    @Column(name = "end_Date", nullable = false)
    private String endDate;

    @Column(name = "auction_status", nullable = false)
    private String auctionStatus;

    @Builder
    public Auction(
            String auctionUuid,
            String productUuid,
            Long startingPrice,
            Long highestBidPrice,
            String highestBidMemberUuid,
            String startDate,
            String endDate,
            String auctionStatus) {
        this.auctionUuid = auctionUuid;
        this.productUuid = productUuid;
        this.startingPrice = startingPrice;
        this.highestBidPrice = highestBidPrice;
        this.highestBidMemberUuid = highestBidMemberUuid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.auctionStatus = auctionStatus;
    }
}
