package com.pieceofcake.auction_service.bid.infrastructure;

import com.pieceofcake.auction_service.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {

    Optional<Bid> findByAuctionUuidAndMemberUuidAndDeletedFalse(String auctionUuid, String memberUuid);
}
