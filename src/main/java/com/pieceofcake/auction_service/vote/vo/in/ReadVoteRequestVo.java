package com.pieceofcake.auction_service.vote.vo.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadVoteRequestVo {
    private String productUuid;

    @Builder
    public ReadVoteRequestVo(String productUuid) {
        this.productUuid = productUuid;
    }

}
