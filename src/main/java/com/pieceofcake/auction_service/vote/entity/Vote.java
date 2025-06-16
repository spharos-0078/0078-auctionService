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

    @Column(name = "starting_price", nullable = false)
    private Long startingPrice;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VoteStatus status;

    @Builder
    public Vote(
            Long id,
            String voteUuid,
            String productUuid,
            Long startingPrice,
            LocalDateTime startDate,
            LocalDateTime endDate,
            VoteStatus status
    ) {
        this.id = id;
        this.voteUuid = voteUuid;
        this.productUuid = productUuid;
        this.startingPrice = startingPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
}
