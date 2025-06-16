package com.pieceofcake.auction_service.vote.application;

import com.pieceofcake.auction_service.vote.dto.in.CreateVoteRequestDto;
import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.infrastructure.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    @Override
    public void createVote(CreateVoteRequestDto createVoteRequestDto) {
        Vote vote = createVoteRequestDto.toEntity();

        voteRepository.save(vote);
    }
}
