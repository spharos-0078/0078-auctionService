package com.pieceofcake.auction_service.vote.vo.in;

import com.pieceofcake.auction_service.vote.entity.enums.VoteChoice;
import lombok.Getter;

@Getter
public class CreateVoteDetailRequestVo {
    private String voteUuid;
    private VoteChoice voteChoice;
}
