package com.pieceofcake.auction_service.vote.application.batch.processor;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import com.pieceofcake.auction_service.auction.infrastructure.client.AuctionFeignClient;
import com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.CreateMoneyWithMemberUuidRequestDto;
import com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.enums.MoneyHistoryType;
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
    private final AuctionService auctionService;
    private final AuctionFeignClient auctionFeignClient;

    /**
     * 프로세스 단계: 투표 데이터 집계 후 새로운 상태 설정
     */
    @Override
    public Vote process(Vote vote) {
        String voteUuid    = vote.getVoteUuid();
        String productUuid = vote.getProductUuid();
        String pieceProductUuid = vote.getPieceProductUuid();

        // 1) 참여자별 투표 내역 조회
        List<VoteDetail> details = voteDetailRepository.findAllByVoteUuid(voteUuid);
        Map<String, VoteChoice> memberVoteMap = details.stream()
                .collect(Collectors.toMap(VoteDetail::getMemberUuid, VoteDetail::getVoteChoice));

        // 2) 조각 수 조회
        MemberPieceResponseWrapper response = pieceFeignClient.getMemberPieceQuantities(pieceProductUuid);
        List<MemberPieceResponseDto> pieceInfos = response.getResult();
        log.info("조각 수 조회 결과: {}", pieceInfos);
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

        if (newStatus == VoteStatus.CLOSED_ACCEPTED) {
            // 5) 찬성이 많으면, auction 생성
            auctionService.createAuction(
                    CreateAuctionRequestDto.builder()
                            .productUuid(vote.getProductUuid())
                            .pieceProductUuid(vote.getPieceProductUuid())
                            .startingPrice(vote.getStartingPrice())
                            .highestBidUuid("최초투표자")
                            .highestBidPrice(vote.getStartingPrice())
                            .highestBidMemberUuid(vote.getStartingMemberUuid())
                            .startDate(vote.getEndDate())
                            .endDate(vote.getEndDate().plusHours(48))
                            .auctionStatus(AuctionStatus.ONGOING)
                            .build()

            );
        } else {
            // 반대가 많으면, 투표참여자의 보증금 반환
            auctionFeignClient.createMoneyWithMemberUuid(
                    CreateMoneyWithMemberUuidRequestDto.builder()
                            .memberUuid(vote.getStartingMemberUuid())
                            .amount(vote.getStartingPrice())
                            .isPositive(true) // 입금 처리
                            .historyType(MoneyHistoryType.FREEZE) // 보증금 환불
                            .moneyHistoryDetail("투표 종료 - 보증금 환불")
                            .build()
            );
        }

        // 6) 변경된 상태를 반영한 Vote 반환 (DB 저장은 writer 단계에서)
        return Vote.builder()
                .id(vote.getId())
                .voteUuid(voteUuid)
                .productUuid(productUuid)
                .pieceProductUuid(pieceProductUuid)
                .startingMemberUuid(vote.getStartingMemberUuid())
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
        items.forEach(vote -> {
            // 투표가 반대(REJECTED)로 끝난 경우에만 이벤트 발행
            if (vote.getStatus() == VoteStatus.CLOSED_REJECTED) {
                kafkaProducer.sendVoteCloseEvent(vote.getPieceProductUuid());
            }
        });
    }

    /**
     * Spring Batch 5에서 onWriteError의 파라미터 순서는 (Exception, Chunk)입니다.
     */
    @Override
    public void onWriteError(Exception exception, Chunk<? extends Vote> items) {
        log.error("투표 처리 중 오류 발생, 이벤트 발행 생략: {}", exception.getMessage());
    }
}
