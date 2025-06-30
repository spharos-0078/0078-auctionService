package com.pieceofcake.auction_service.vote.dto.in;

import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import com.pieceofcake.auction_service.vote.vo.in.CreateVoteRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateVoteRequestDto {
    private String pieceProductUuid;
    private String productUuid;
    private String startingMemberUuid;
    private Long startingPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder
    public CreateVoteRequestDto(
            String pieceProductUuid,
            String productUuid,
            String startingMemberUuid,
            Long startingPrice,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        this.pieceProductUuid = pieceProductUuid;
        this.productUuid = productUuid;
        this.startingMemberUuid = startingMemberUuid;
        this.startingPrice = startingPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static CreateVoteRequestDto from(CreateVoteRequestVo createVoteRequestVo, String memberUuid) {
        return CreateVoteRequestDto.builder()
                .pieceProductUuid(createVoteRequestVo.getPieceProductUuid())
                .productUuid(createVoteRequestVo.getProductUuid())
                .startingMemberUuid(memberUuid)
                .startingPrice(createVoteRequestVo.getStartingPrice())
                .startDate(createVoteRequestVo.getStartDate())
                .endDate(createVoteRequestVo.getEndDate())
                .build();
    }

    public Vote toEntity() {
        return Vote.builder()
                .voteUuid(UUID.randomUUID().toString())
                .pieceProductUuid(this.pieceProductUuid)
                .productUuid(this.productUuid)
                .startingMemberUuid(this.startingMemberUuid)
                .startingPrice(this.startingPrice)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status(VoteStatus.OPEN)
                .build();
    }

}
