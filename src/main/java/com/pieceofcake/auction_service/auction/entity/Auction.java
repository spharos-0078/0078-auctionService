package com.pieceofcake.auction_service.auction.entity;

import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "auction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auction_uuid", nullable = false)
    private String auctionUuid;

    @Column(name = "productUuid", nullable = false)
    private String productUuid;

    @Column(name = "starting_price", nullable = false)
    private Long startingPrice;

    @Column(name = "highest_bid_uuid")
    private String highestBidUuid;

    @Column(name = "highest_bid_price")
    private Long highestBidPrice;

    @Column(name = "highest_bid_member_uuid")
    private String highestBidMemberUuid;

    @Column(name = "start_Date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_Date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "auction_status")
    private AuctionStatus auctionStatus;

    @Builder
    public Auction(
            Long id,
            String auctionUuid,
            String productUuid,
            Long startingPrice,
            String highestBidUuid,
            Long highestBidPrice,
            String highestBidMemberUuid,
            LocalDateTime startDate,
            LocalDateTime endDate,
            AuctionStatus auctionStatus
    ) {
        this.id = id;
        this.auctionUuid = auctionUuid;
        this.productUuid = productUuid;
        this.startingPrice = startingPrice;
        this.highestBidUuid = highestBidUuid;
        this.highestBidPrice = highestBidPrice;
        this.highestBidMemberUuid = highestBidMemberUuid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.auctionStatus = auctionStatus;
    }
}
