package com.pieceofcake.auction_service.vote.application;

import com.pieceofcake.auction_service.vote.dto.in.CreateVoteRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteRequestdto;
import com.pieceofcake.auction_service.vote.dto.out.ReadVoteResponseDto;

public interface VoteService {
    void createVote(CreateVoteRequestDto createVoteRequestDto);
    ReadVoteResponseDto readVote(ReadVoteRequestdto readVoteRequestDto);
}
