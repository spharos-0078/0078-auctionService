package com.pieceofcake.auction_service.vote.application;

import com.pieceofcake.auction_service.vote.dto.in.CreateVoteRequestDto;

public interface VoteService {
    void createVote(CreateVoteRequestDto createVoteRequestDto);
}
