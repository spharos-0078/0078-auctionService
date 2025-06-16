package com.pieceofcake.auction_service.vote.infrastructure;

import com.pieceofcake.auction_service.vote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

}
