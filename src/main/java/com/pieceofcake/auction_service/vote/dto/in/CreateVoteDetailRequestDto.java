package com.pieceofcake.auction_service.vote.dto.in;

import com.pieceofcake.auction_service.vote.entity.VoteDetail;
import com.pieceofcake.auction_service.vote.entity.enums.VoteChoice;
import com.pieceofcake.auction_service.vote.vo.in.CreateVoteDetailRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateVoteDetailRequestDto {
    private String voteUuid;
    private String memberUuid;
    private VoteChoice voteChoice;

    @Builder
    public CreateVoteDetailRequestDto(
            String voteUuid,
            String memberUuid,
            VoteChoice voteChoice
    ) {
        this.voteUuid = voteUuid;
        this.memberUuid = memberUuid;
        this.voteChoice = voteChoice;
    }

    public static CreateVoteDetailRequestDto from(
            CreateVoteDetailRequestVo createVoteDetailRequestVo,
            String memberUuid
    ) {
        return CreateVoteDetailRequestDto.builder()
                .voteUuid(createVoteDetailRequestVo.getVoteUuid())
                .memberUuid(memberUuid)
                .voteChoice(createVoteDetailRequestVo.getVoteChoice())
                .build();
    }

    public VoteDetail toEntity() {
        return VoteDetail.builder()
                .voteDetailUuid(UUID.randomUUID().toString())
                .voteUuid(voteUuid)
                .memberUuid(memberUuid)
                .voteChoice(voteChoice)
                .build();
    }
}
