package com.pieceofcake.auction_service.auction.infrastructure;

import com.pieceofcake.auction_service.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<Auction> findByAuctionUuid(String auctionUuid);
}
