package com.pieceofcake.auction_service.vote.infrastructure;

import com.pieceofcake.auction_service.vote.entity.VoteDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteDetailRepository extends JpaRepository<VoteDetail, Long> {

    List<VoteDetail> findAllByVoteUuid(String voteUuid);

    Boolean existsByVoteUuidAndMemberUuid(String voteUuid, String memberUuid);

    Optional<VoteDetail>findByVoteUuidAndMemberUuid(String voteUuid, String memberUuid);
}
