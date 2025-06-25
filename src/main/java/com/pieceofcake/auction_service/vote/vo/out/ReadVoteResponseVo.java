package com.pieceofcake.auction_service.vote.vo.out;

import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReadVoteResponseVo {
    private String voteUuid;
    private String productUuid;
    private String startingMemberUuid;
    private Long startingPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private VoteStatus status;
    private Long agreeCount;
    private Long disagreeCount;
    private Long noVoteCount;
    private Long totalCount;

    @Builder
    public ReadVoteResponseVo(
            String voteUuid,
            String productUuid,
            String startingMemberUuid,
            Long startingPrice,
            LocalDateTime startDate,
            LocalDateTime endDate,
            VoteStatus status,
            Long agreeCount,
            Long disagreeCount,
            Long noVoteCount,
            Long totalCount
    ) {
        this.voteUuid = voteUuid;
        this.productUuid = productUuid;
        this.startingMemberUuid = startingMemberUuid;
        this.startingPrice = startingPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
        this.noVoteCount = noVoteCount;
        this.totalCount = totalCount;
    }
}
