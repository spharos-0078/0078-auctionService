package com.pieceofcake.auction_service.auction.infrastructure;

import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<Auction> findByAuctionUuid(String auctionUuid);

    Boolean existsByProductUuid(String productUuid);

    @Modifying
    @Query("UPDATE Auction a SET a.highestBidUuid = :highestBidUuid, a.highestBidPrice = :highestBidPrice, a.highestBidMemberUuid = :highestBidMemberUuid WHERE a.auctionUuid = :auctionUuid")
    void updateHighestBidInfo(@Param("auctionUuid") String auctionUuid,
                              @Param("highestBidUuid") String highestBidUuid,
                              @Param("highestBidPrice") Long highestBidPrice,
                              @Param("highestBidMemberUuid") String highestBidMemberUuid
    );

    List<Auction> findAllByAuctionStatusAndEndDateAfter(AuctionStatus auctionStatus, LocalDateTime endDate);

    List<Auction> findAllByAuctionStatus(AuctionStatus auctionStatus);
}
