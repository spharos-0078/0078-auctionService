package com.pieceofcake.auction_service.vote.entity;

import com.pieceofcake.auction_service.common.entity.BaseEntity;
import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bouncycastle.jcajce.provider.symmetric.Serpent;

import java.time.LocalDateTime;

@Getter
@Table(name = "vote")
@NoArgsConstructor
@Entity
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_uuid", nullable = false)
    private String voteUuid;

    @Column(name = "product_uuid", nullable = false)
    private String productUuid;

    @Column(name = "starting_member_uuid", nullable = false)
    private String startingMemberUuid;

    @Column(name = "starting_price", nullable = false)
    private Long startingPrice;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VoteStatus status;

    @Column(name = "agree_count")
    private Long agreeCount;

    @Column(name = "disagree_count")
    private Long disagreeCount;

    @Column(name = "no_vote_count")
    private Long noVoteCount;

    @Column(name = "total_count")
    private Long totalCount;

    @Builder
    public Vote(
            Long id,
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
        this.id = id;
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
