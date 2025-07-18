package com.pieceofcake.auction_service.vote.application;

import com.pieceofcake.auction_service.vote.dto.in.CreateVoteRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteRequestdto;
import com.pieceofcake.auction_service.vote.dto.out.ReadVoteListResponseDto;
import com.pieceofcake.auction_service.vote.dto.out.ReadVoteResponseDto;

import java.util.List;

public interface VoteService {
    void createVote(CreateVoteRequestDto createVoteRequestDto);
    ReadVoteResponseDto readVote(ReadVoteRequestdto readVoteRequestDto);
    List<ReadVoteListResponseDto> readVoteList(String status);
}
