package com.pieceofcake.auction_service.vote.vo.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadVoteDetailRequestVo {
    private String voteUuid;
    private String memberUuid;

    @Builder
    public ReadVoteDetailRequestVo(String voteUuid, String memberUuid) {
        this.voteUuid = voteUuid;
        this.memberUuid = memberUuid;
    }
}
