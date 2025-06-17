package com.pieceofcake.auction_service.vote.application;

import com.pieceofcake.auction_service.vote.dto.in.CreateVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.dto.out.ReadVoteDetailResponseDto;

public interface VoteDetailService {
    void createVoteDetail(CreateVoteDetailRequestDto createVoteDetailRequestDto);
    ReadVoteDetailResponseDto readVoteDetail(ReadVoteDetailRequestDto readVoteDetailRequestDto);
}
