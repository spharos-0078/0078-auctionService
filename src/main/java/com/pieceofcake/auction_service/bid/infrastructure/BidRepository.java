package com.pieceofcake.auction_service.bid.infrastructure;

import com.pieceofcake.auction_service.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {


}
