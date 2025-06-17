package com.pieceofcake.auction_service.vote.application;

import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import com.pieceofcake.auction_service.vote.dto.in.CreateVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.entity.VoteDetail;
import com.pieceofcake.auction_service.vote.infrastructure.VoteDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteDetailServiceImpl implements VoteDetailService{

    private final VoteDetailRepository voteDetailRepository;

    @Override
    public void createVoteDetail(CreateVoteDetailRequestDto createVoteDetailRequestDto) {
        // 투표 내역 조회
        Optional<VoteDetail> oldVoteDetailOptional = voteDetailRepository.findByVoteUuidAndMemberUuid(
                createVoteDetailRequestDto.getVoteUuid(),
                createVoteDetailRequestDto.getMemberUuid()
        );

        if (oldVoteDetailOptional.isPresent()) {
            // 투표 내역이 있고 값이 다르면 수정
            VoteDetail oldVoteDetail = oldVoteDetailOptional.get();

            if (oldVoteDetail.getVoteChoice() != createVoteDetailRequestDto.getVoteChoice()) {
                voteDetailRepository.save(
                        VoteDetail.builder()
                                .id(oldVoteDetail.getId())
                                .voteDetailUuid(oldVoteDetail.getVoteDetailUuid())
                                .voteUuid(createVoteDetailRequestDto.getVoteUuid())
                                .memberUuid(createVoteDetailRequestDto.getMemberUuid())
                                .voteChoice(createVoteDetailRequestDto.getVoteChoice())
                                .build()
                );
            }
        } else {
            // 투표 내역이 없으면 새로 생성
            voteDetailRepository.save(createVoteDetailRequestDto.toEntity());
        }
    }

}
