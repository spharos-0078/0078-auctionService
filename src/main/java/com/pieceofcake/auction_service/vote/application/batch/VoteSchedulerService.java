package com.pieceofcake.auction_service.vote.application.batch;

import com.pieceofcake.auction_service.vote.application.VoteService;
import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.entity.VoteDetail;
import com.pieceofcake.auction_service.vote.entity.enums.VoteChoice;
import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import com.pieceofcake.auction_service.vote.infrastructure.VoteDetailRepository;
import com.pieceofcake.auction_service.vote.infrastructure.VoteRepository;
import com.pieceofcake.auction_service.vote.infrastructure.client.PieceFeignClient;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.in.MemberPieceRequestDto;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteSchedulerService {

    private final VoteService voteService;
    private final PieceFeignClient pieceFeignClient;
    private final VoteRepository voteRepository;
    private final VoteDetailRepository voteDetailRepository;

    @Scheduled(cron = "0 * * * * *") // 매 분 0초에 실행
    @Transactional
    public void closeAndCountVotes() {
        List<Vote> expiredVotes = voteRepository.findAllByStatusAndEndDateBefore(VoteStatus.OPEN, LocalDateTime.now());

        for (Vote vote : expiredVotes) {
            String voteUuid = vote.getVoteUuid();
            String productUuid = vote.getProductUuid();

            // 해당 투표에 참여한 유저의 찬반 정보 수집
            List<VoteDetail> details = voteDetailRepository.findAllByVoteUuid(voteUuid);

            Map<String, VoteChoice> memberVoteMap = details.stream()
                    .collect(Collectors.toMap(VoteDetail::getMemberUuid, VoteDetail::getVoteChoice));


            // 피스 서비스에서 각 memberUuid가 가진 조각 수 조회
            List<MemberPieceResponseDto> pieceInfos = pieceFeignClient.getMemberPieceQuantities(
                    MemberPieceRequestDto.from(productUuid)
            );

            // 조각 수에 따라 찬반 투표 수 계산
            long agreeCount = 0;
            long disagreeCount = 0;
            long noVoteCount = 0;
            long total = 0;

            for (MemberPieceResponseDto info : pieceInfos) {
                String memberUuid = info.getMemberUuid();
                int quantity = info.getQuantity();
                total += quantity;

                VoteChoice choice = memberVoteMap.get(memberUuid);
                if (choice == VoteChoice.AGREE) agreeCount += quantity;
                else if (choice == VoteChoice.DISAGREE) disagreeCount += quantity;
                else noVoteCount += quantity; // 명시적으로 abstain 선택 시
            }

            double agreeRate = total > 0 ? (agreeCount * 100.0 / total) : 0.0;
            double disagreeRate = total > 0 ? (disagreeCount * 100.0 / total) : 0.0;
            double noVoteRate = total > 0 ? (noVoteCount * 100.0 / total) : 0.0;

            // 투표 결과에 따라 상태 결정
            VoteStatus newStatus;
            if (agreeRate >= 50.0) { // 50% 이상 찬성이면 승인
                newStatus = VoteStatus.CLOSED_ACCEPTED;
            } else {
                newStatus = VoteStatus.CLOSED_REJECTED;
            }

            // vote 상태 업데이트
            Vote newVote = Vote.builder()
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

            voteRepository.save(newVote);
        }


    }


}
