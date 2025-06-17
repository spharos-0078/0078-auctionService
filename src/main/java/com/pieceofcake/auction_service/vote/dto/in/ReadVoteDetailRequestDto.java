package com.pieceofcake.auction_service.vote.dto.in;

import com.pieceofcake.auction_service.vote.vo.in.ReadVoteDetailRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadVoteDetailRequestDto {
    private String memberUuid;
    private String voteUuid;

    @Builder
    public ReadVoteDetailRequestDto(String memberUuid, String voteUuid) {
        this.memberUuid = memberUuid;
        this.voteUuid = voteUuid;
    }

    public static ReadVoteDetailRequestDto from(
            ReadVoteDetailRequestVo readVoteDetailRequestVo
    ) {
        return ReadVoteDetailRequestDto.builder()
                .memberUuid(readVoteDetailRequestVo.getMemberUuid())
                .voteUuid(readVoteDetailRequestVo.getVoteUuid())
                .build();
    }
}
