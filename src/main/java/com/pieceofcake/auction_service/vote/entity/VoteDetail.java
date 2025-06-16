package com.pieceofcake.auction_service.vote.entity;

import com.pieceofcake.auction_service.vote.entity.enums.VoteChoice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "vote_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoteDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_detail_uuid", nullable = false)
    private String voteDetailUuid;

    @Column(name = "vote_uuid", nullable = false)
    private String voteUuid;

    @Column(name = "member_uuid", nullable = false)
    private String memberUuid;

    @Column(name = "quantity", nullable = false)
    public Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_choice", nullable = false)
    private VoteChoice voteChoice;

    @Builder
    public VoteDetail(String voteDetailUuid, String voteUuid, String memberUuid, Long quantity, VoteChoice voteChoice) {
        this.voteDetailUuid = voteDetailUuid;
        this.voteUuid = voteUuid;
        this.memberUuid = memberUuid;
        this.quantity = quantity;
        this.voteChoice = voteChoice;
    }
}

