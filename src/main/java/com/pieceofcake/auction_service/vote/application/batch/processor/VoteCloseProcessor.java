package com.pieceofcake.auction_service.vote.application.batch.processor;

import com.pieceofcake.auction_service.kafka.producer.KafkaProducer;
import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.entity.VoteDetail;
import com.pieceofcake.auction_service.vote.entity.enums.VoteChoice;
import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import com.pieceofcake.auction_service.vote.infrastructure.VoteDetailRepository;
import com.pieceofcake.auction_service.vote.infrastructure.client.PieceFeignClient;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseDto;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class VoteCloseProcessor
        implements ItemProcessor<Vote, Vote>, ItemWriteListener<Vote> {

    private final VoteDetailRepository voteDetailRepository;
    private final PieceFeignClient pieceFeignClient;
    private final KafkaProducer kafkaProducer;

    /**
     * 프로세스 단계: 투표 데이터 집계 후 새로운 상태 설정
     */
    @Override
    public Vote process(Vote vote) {
        String voteUuid    = vote.getVoteUuid();
        String productUuid = vote.getProductUuid();

        // 1) 참여자별 투표 내역 조회
        List<VoteDetail> details = voteDetailRepository.findAllByVoteUuid(voteUuid);
        Map<String, VoteChoice> memberVoteMap = details.stream()
                .collect(Collectors.toMap(VoteDetail::getMemberUuid, VoteDetail::getVoteChoice));

        // 2) 조각 수 조회
        MemberPieceResponseWrapper response = pieceFeignClient.getMemberPieceQuantities(productUuid);
        List<MemberPieceResponseDto> pieceInfos = response.getResult();

        // 3) 가중치 집계
        long agreeCount = 0, disagreeCount = 0, noVoteCount = 0, total = 0;
        for (MemberPieceResponseDto info : pieceInfos) {
            int qty = info.getPieceQuantity();
            total += qty;
            VoteChoice choice = memberVoteMap.get(info.getMemberUuid());
            if      (choice == VoteChoice.AGREE)    agreeCount += qty;
            else if (choice == VoteChoice.DISAGREE) disagreeCount += qty;
            else                                     noVoteCount  += qty;
        }

        // 4) 결과 비율 계산 및 상태 결정
        double agreeRate = total > 0 ? (agreeCount * 100.0 / total) : 0.0;
        VoteStatus newStatus = (agreeRate >= 50.0)
                ? VoteStatus.CLOSED_ACCEPTED
                : VoteStatus.CLOSED_REJECTED;

        // 5) 변경된 상태를 반영한 Vote 반환 (DB 저장은 writer 단계에서)
        return Vote.builder()
                .id(vote.getId())
                .voteUuid(voteUuid)
                .productUuid(productUuid)
                .startingPrice(vote.getStartingPrice())
                .startDate(vote.getStartDate())
                .endDate(vote.getEndDate())
                .status(newStatus)
                .agreeCount(agreeCount)
                .disagreeCount(disagreeCount)
                .noVoteCount(noVoteCount)
                .totalCount(total)
                .build();
    }

    /**
     * 반드시 throws Exception 을 추가하고,
     * 파라미터 순서도 List<? extends Vote>, Exception 으로 맞춥니다.
     */
    @Override
    public void beforeWrite(Chunk<? extends Vote> items) {
        // no-op
    }

    @Override
    public void afterWrite(Chunk<? extends Vote> items) {
        items.forEach(vote ->
                kafkaProducer.sendVoteCloseEvent(vote.getProductUuid())
        );
    }

    /**
     * Spring Batch 5에서 onWriteError의 파라미터 순서는 (Exception, Chunk)입니다.
     */
    @Override
    public void onWriteError(Exception exception, Chunk<? extends Vote> items) {
        log.error("투표 처리 중 오류 발생, 이벤트 발행 생략: {}", exception.getMessage());
    }
}
