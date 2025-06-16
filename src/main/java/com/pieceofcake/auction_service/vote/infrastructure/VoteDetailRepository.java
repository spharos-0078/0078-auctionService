package com.pieceofcake.auction_service.vote.infrastructure;

import com.pieceofcake.auction_service.vote.entity.VoteDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteDetailRepository extends JpaRepository<VoteDetail, Long> {

    List<VoteDetail> findAllByVoteUuid(String voteUuid);


}
