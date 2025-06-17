package com.pieceofcake.auction_service.vote.application;

import com.pieceofcake.auction_service.vote.dto.in.CreateVoteDetailRequestDto;

public interface VoteDetailService {
    void createVoteDetail(CreateVoteDetailRequestDto createVoteDetailRequestDto);
}
