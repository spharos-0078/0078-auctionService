package com.pieceofcake.auction_service.vote.dto.out;

import com.pieceofcake.auction_service.vote.entity.VoteDetail;
import com.pieceofcake.auction_service.vote.entity.enums.VoteChoice;
import com.pieceofcake.auction_service.vote.vo.out.ReadVoteDetailResponseVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadVoteDetailResponseDto {
    private String voteUuid;
    private VoteChoice voteChoice;

    @Builder
    public ReadVoteDetailResponseDto(String voteUuid, VoteChoice voteChoice) {
        this.voteUuid = voteUuid;
        this.voteChoice = voteChoice;
    }

    public static ReadVoteDetailResponseDto from(VoteDetail voteDetail) {
        return ReadVoteDetailResponseDto.builder()
                .voteUuid(voteDetail.getVoteUuid())
                .voteChoice(voteDetail.getVoteChoice())
                .build();
    }

    public ReadVoteDetailResponseVo toVo() {
        return ReadVoteDetailResponseVo.builder()
                .voteUuid(this.voteUuid)
                .voteChoice(this.voteChoice)
                .build();
    }
}
