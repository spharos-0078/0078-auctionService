package com.pieceofcake.auction_service.vote.application;

import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import com.pieceofcake.auction_service.kafka.producer.KafkaProducer;
import com.pieceofcake.auction_service.vote.dto.in.CreateVoteRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteRequestdto;
import com.pieceofcake.auction_service.vote.dto.out.ReadVoteListResponseDto;
import com.pieceofcake.auction_service.vote.dto.out.ReadVoteResponseDto;
import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import com.pieceofcake.auction_service.vote.infrastructure.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final KafkaProducer kafkaProducer;
    @Override
    @Transactional
    public void createVote(CreateVoteRequestDto createVoteRequestDto) {
        Vote vote = createVoteRequestDto.toEntity();

        voteRepository.save(vote);
        // 투표 시작 이벤트 발행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                kafkaProducer.sendVoteStartEvent(vote.getProductUuid());
            }
        });
    }

    @Override
    public ReadVoteResponseDto readVote(ReadVoteRequestdto readVoteRequestDto) {
        Vote vote = voteRepository.findFirstByProductUuidOrderByIdDesc(readVoteRequestDto.getProductUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.VOTE_NOT_FOUND));

        return ReadVoteResponseDto.from(vote);
    }

    @Override
    public List<ReadVoteListResponseDto> readVoteList(String status) {
        List<Vote> votes;

        if (status != null && !status.isEmpty()) {
            try {
                VoteStatus voteStatus = VoteStatus.valueOf(status);
                votes = voteRepository.findAllByStatus(voteStatus);
            } catch (IllegalArgumentException e) {
                // 유효하지 않은 status 값이 전달된 경우
                throw new BaseException(BaseResponseStatus.INVALID_VOTE_STATUS);
            }
        } else {
            // status 파라미터가 없으면 모든 투표를 반환
            votes = voteRepository.findAll();
        }

        return votes
                .stream()
                .map(ReadVoteListResponseDto::from)
                .toList();
    }
}
