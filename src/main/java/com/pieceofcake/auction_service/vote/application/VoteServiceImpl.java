package com.pieceofcake.auction_service.vote.application;

import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import com.pieceofcake.auction_service.kafka.producer.KafkaProducer;
import com.pieceofcake.auction_service.vote.dto.in.CreateVoteRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteRequestdto;
import com.pieceofcake.auction_service.vote.dto.out.ReadVoteResponseDto;
import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.infrastructure.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
                kafkaProducer.sendAuctionStartEvent(vote.getProductUuid());
            }
        });
    }

    @Override
    public ReadVoteResponseDto readVote(ReadVoteRequestdto readVoteRequestDto) {
        Vote vote = voteRepository.findFirstByProductUuidOrderByIdDesc(readVoteRequestDto.getProductUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.VOTE_NOT_FOUND));

        return ReadVoteResponseDto.from(vote);
    }
}
