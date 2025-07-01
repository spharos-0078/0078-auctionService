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

    @Builder
    public CreateVoteRequestDto(
            String pieceProductUuid,
            String productUuid,
            String startingMemberUuid,
            Long startingPrice
    ) {
        this.pieceProductUuid = pieceProductUuid;
        this.productUuid = productUuid;
        this.startingMemberUuid = startingMemberUuid;
        this.startingPrice = startingPrice;
    }

    public static CreateVoteRequestDto from(CreateVoteRequestVo createVoteRequestVo, String memberUuid) {
        return CreateVoteRequestDto.builder()
                .pieceProductUuid(createVoteRequestVo.getPieceProductUuid())
                .productUuid(createVoteRequestVo.getProductUuid())
                .startingMemberUuid(memberUuid)
                .startingPrice(createVoteRequestVo.getStartingPrice())
                .build();
    }

    public Vote toEntity() {
        // 현재 시각을 시작 시간으로 설정
        LocalDateTime now = LocalDateTime.now();

        // 종료 시간은 24시간 후의 시각에서 다음 정시로 올림
        LocalDateTime endTimeRaw = now.plusHours(24);
        // 다음 정시로 올림 (예: 15:20 -> 16:00)
        LocalDateTime endTime = endTimeRaw
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        endTime = endTime.plusHours(1);

        return Vote.builder()
                .voteUuid(UUID.randomUUID().toString())
                .pieceProductUuid(this.pieceProductUuid)
                .productUuid(this.productUuid)
                .startingMemberUuid(this.startingMemberUuid)
                .startingPrice(this.startingPrice)
                .startDate(now)
                .endDate(endTime)
                .status(VoteStatus.OPEN)
                .build();
    }

}
