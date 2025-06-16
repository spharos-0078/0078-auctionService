package com.pieceofcake.auction_service.bid.entity;

import com.pieceofcake.auction_service.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "bid")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Bid extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(name = "bid_uuid", nullable = false)
    private String bidUuid;

    @Column(name = "auction_uuid", nullable = false)
    private String auctionUuid;

    @Column(name = "member_uuid", nullable = false)
    private String memberUuid;

    @Column(name = "bid_price", nullable = false)
    private Long bidPrice;

    @Column(name = "is_highest_bid")
    private Boolean isHighestBid;

    @Column(name = "hidden")
    private Boolean hidden;

    @Builder
    public Bid(
            Long id,
            String bidUuid,
            String auctionUuid,
            String memberUuid,
            Long bidPrice,
            Boolean isHighestBid,
            Boolean hidden
    ) {
        this.id = id;
        this.bidUuid = bidUuid;
        this.auctionUuid = auctionUuid;
        this.memberUuid = memberUuid;
        this.bidPrice = bidPrice;
        this.isHighestBid = isHighestBid;
        this.hidden = hidden;
    }

}
