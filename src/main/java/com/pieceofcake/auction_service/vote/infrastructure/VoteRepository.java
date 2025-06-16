package com.pieceofcake.auction_service.vote.infrastructure;

import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findAllByStatusAndEndDateBefore(VoteStatus status, LocalDateTime endDate);
}
