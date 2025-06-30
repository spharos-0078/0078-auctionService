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
    private String pieceProductUuid;

    @Builder
    public ReadVoteListResponseVo(
            String voteUuid,
            String productUuid,
            String pieceProductUuid
    ) {
        this.voteUuid = voteUuid;
        this.productUuid = productUuid;
        this.pieceProductUuid = pieceProductUuid;
    }
}
