package com.pieceofcake.auction_service.vote.vo.out;

import com.pieceofcake.auction_service.vote.entity.enums.VoteChoice;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadVoteDetailResponseVo {
    private String voteUuid;
    private VoteChoice voteChoice;

    @Builder
    public ReadVoteDetailResponseVo(String voteUuid, VoteChoice voteChoice) {
        this.voteUuid = voteUuid;
        this.voteChoice = voteChoice;
    }
}
