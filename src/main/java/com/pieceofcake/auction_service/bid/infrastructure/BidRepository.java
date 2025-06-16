package com.pieceofcake.auction_service.bid.infrastructure;

import com.pieceofcake.auction_service.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    Optional<Bid> findByAuctionUuidAndMemberUuidAndDeletedFalse(String auctionUuid, String memberUuid);

    @Query("SELECT DISTINCT b.auctionUuid FROM Bid b WHERE b.memberUuid = :memberUuid")
    List<String> findDistinctAuctionUuidsByMemberUuid(@Param("memberUuid") String memberUuid);

    List<Bid> findAllByAuctionUuidAndDeletedFalse(String auctionUuid);

    Optional<Bid> findByBidUuidAndMemberUuidAndDeletedFalse(String bidUuid, String memberUuid);
}
