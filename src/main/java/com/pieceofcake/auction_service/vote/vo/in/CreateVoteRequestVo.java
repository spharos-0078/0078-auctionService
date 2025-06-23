package com.pieceofcake.auction_service.vote.vo.in;

import com.pieceofcake.auction_service.vote.entity.enums.VoteStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateVoteRequestVo {

    private String productUuid;
    private Long startingPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder
    public CreateVoteRequestVo(
            String productUuid,
            Long startingPrice,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        this.productUuid = productUuid;
        this.startingPrice = startingPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
