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

    @Column(name = "starting_date", nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime startingDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_status", nullable = false)
    private VoteStatus voteStatus;

    @Builder
    public Vote(String voteUuid, String productUuid, LocalDateTime startingDate, LocalDateTime endDate, VoteStatus voteStatus) {
        this.voteUuid = voteUuid;
        this.productUuid = productUuid;
        this.startingDate = startingDate;
        this.endDate = endDate;
        this.voteStatus = voteStatus;
    }



}
