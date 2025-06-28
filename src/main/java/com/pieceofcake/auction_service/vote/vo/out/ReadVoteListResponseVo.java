package com.pieceofcake.auction_service.vote.vo.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReadVoteListResponseVo {
    private String voteUuid;
    private String productUuid;

    @Builder
    public ReadVoteListResponseVo(
            String voteUuid,
            String productUuid
    ) {
        this.voteUuid = voteUuid;
        this.productUuid = productUuid;
    }
}
