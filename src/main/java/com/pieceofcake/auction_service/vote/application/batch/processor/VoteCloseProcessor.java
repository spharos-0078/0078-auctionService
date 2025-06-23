package com.pieceofcake.auction_service.vote.application.batch.processor;

import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.entity.VoteDetail;
import com.pieceofcake.auction_service.vote.entity.enums.VoteChoice;
import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import com.pieceofcake.auction_service.vote.infrastructure.VoteDetailRepository;
import com.pieceofcake.auction_service.vote.infrastructure.client.PieceFeignClient;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.in.MemberPieceRequestDto;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseDto;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
public class VoteCloseProcessor implements ItemProcessor<Vote, Vote> {

    // 투표 상세 정보 조회용 Repository
    private final VoteDetailRepository voteDetailRepository;

    // 피스 개수 조회를 위한 외부 API Client
    private final PieceFeignClient pieceFeignClient;

    /**
     * 하나의 Vote에 대해 처리 수행
     */
    @Override
    public Vote process(Vote vote) throws Exception {
        String voteUuid = vote.getVoteUuid();
        String productUuid = vote.getProductUuid();

        // 해당 투표에 참여한 모든 유저의 찬반 정보 조회
        List<VoteDetail> details = voteDetailRepository.findAllByVoteUuid(voteUuid);

        // memberUuid -> AGREE / DISAGREE / ABSTAIN 등 매핑
        Map<String, VoteChoice> memberVoteMap = details.stream()
                .collect(Collectors.toMap(VoteDetail::getMemberUuid, VoteDetail::getVoteChoice));

        // 외부 피스 서비스에서 유저가 가진 조각 수 조회
        MemberPieceResponseWrapper response = pieceFeignClient.getMemberPieceQuantities(productUuid);
        List<MemberPieceResponseDto> pieceInfos = response.getResult();
        log.info("@@@@@@@@@@@@@: {}", productUuid);

        // 투표 결과 집계용 변수
        long agreeCount = 0, disagreeCount = 0, noVoteCount = 0, total = 0;

        // 각 유저의 조각 수를 바탕으로 찬/반/무응답 수 계산
        for (MemberPieceResponseDto info : pieceInfos) {
            String memberUuid = info.getMemberUuid();
            log.info("###1Processing member: {}, quantity: {}", memberUuid, info.getPieceQuantity());
            int quantity = info.getPieceQuantity(); // 해당 유저가 가진 조각 수
            total += quantity;

            // 찬/반 여부에 따라 카운팅
            VoteChoice choice = memberVoteMap.get(memberUuid);
            if (choice == VoteChoice.AGREE) agreeCount += quantity;
            else if (choice == VoteChoice.DISAGREE) disagreeCount += quantity;
            else noVoteCount += quantity;
        }

        // 비율 계산 (예외 방지를 위해 분모 체크)
        double agreeRate = total > 0 ? (agreeCount * 100.0 / total) : 0.0;

        // 50% 이상 찬성 시 승인
        VoteStatus newStatus = agreeRate >= 50.0
                ? VoteStatus.CLOSED_ACCEPTED
                : VoteStatus.CLOSED_REJECTED;

        // 새 상태를 반영한 Vote 객체 생성
        return Vote.builder()
                .id(vote.getId())
                .voteUuid(vote.getVoteUuid())
                .productUuid(vote.getProductUuid())
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
}
