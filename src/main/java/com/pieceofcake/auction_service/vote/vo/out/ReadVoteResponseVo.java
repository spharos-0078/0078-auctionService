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
    private Long startingPrice;
    private String startDate;
    private String endDate;
    private VoteStatus status;
    private Long agreeCount;
    private Long disagreeCount;
    private Long noVoteCount;
    private Long totalCount;

    @Builder
    public ReadVoteResponseVo(String voteUuid, String productUuid, Long startingPrice,
                              String startDate, String endDate, VoteStatus status,
                              Long agreeCount, Long disagreeCount, Long noVoteCount, Long totalCount) {
        this.voteUuid = voteUuid;
        this.productUuid = productUuid;
        this.startingPrice = startingPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.agreeCount = agreeCount != null ? agreeCount : 0L;
        this.disagreeCount = disagreeCount != null ? disagreeCount : 0L;
        this.noVoteCount = noVoteCount != null ? noVoteCount : 0L;
        this.totalCount = totalCount != null ? totalCount : 0L;
    }
}
