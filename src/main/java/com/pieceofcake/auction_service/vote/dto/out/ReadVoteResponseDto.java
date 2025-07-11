package com.pieceofcake.auction_service.vote.dto.out;

import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import com.pieceofcake.auction_service.vote.vo.out.ReadVoteResponseVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReadVoteResponseDto {
    private String voteUuid;
    private String productUuid;
    private String pieceProductUuid;
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
    public ReadVoteResponseDto(
            String voteUuid,
            String productUuid,
            String pieceProductUuid,
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
        this.pieceProductUuid = pieceProductUuid;
        this.startingMemberUuid = startingMemberUuid;
        this.startingPrice = startingPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.agreeCount = agreeCount != null ? agreeCount : 0L;
        this.disagreeCount = disagreeCount != null ? disagreeCount : 0L;
        this.noVoteCount = noVoteCount != null ? noVoteCount : 0L;
        this.totalCount = totalCount != null ? totalCount : 0L;
    }

    public static ReadVoteResponseDto from(Vote vote) {
        return ReadVoteResponseDto.builder()
                .voteUuid(vote.getVoteUuid())
                .productUuid(vote.getProductUuid())
                .pieceProductUuid(vote.getPieceProductUuid())
                .startingMemberUuid(vote.getStartingMemberUuid())
                .startingPrice(vote.getStartingPrice())
                .startDate(vote.getStartDate())
                .endDate(vote.getEndDate())
                .status(vote.getStatus())
                .agreeCount(vote.getAgreeCount())
                .disagreeCount(vote.getDisagreeCount())
                .noVoteCount(vote.getNoVoteCount())
                .totalCount(vote.getTotalCount())
                .build();
    }

    public ReadVoteResponseVo toVo() {
        return ReadVoteResponseVo.builder()
                .voteUuid(this.voteUuid)
                .productUuid(this.productUuid)
                .pieceProductUuid(this.pieceProductUuid)
                .startingMemberUuid(this.startingMemberUuid)
                .startingPrice(this.startingPrice)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status(this.status)
                .agreeCount(this.agreeCount)
                .disagreeCount(this.disagreeCount)
                .noVoteCount(this.noVoteCount)
                .totalCount(this.totalCount)
                .build();
    }
}
