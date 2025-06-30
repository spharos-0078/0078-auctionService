package com.pieceofcake.auction_service.vote.dto.out;

import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.vo.out.ReadVoteListResponseVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReadVoteListResponseDto {
    private String voteUuid;
    private String productUuid;
    private String pieceProductUuid;

    @Builder
    public ReadVoteListResponseDto(
            String voteUuid,
            String productUuid,
            String pieceProductUuid
    ) {
        this.voteUuid = voteUuid;
        this.productUuid = productUuid;
        this.pieceProductUuid = pieceProductUuid;
    }

    public static ReadVoteListResponseDto from(Vote vote) {
        return ReadVoteListResponseDto.builder()
                .voteUuid(vote.getVoteUuid())
                .productUuid(vote.getProductUuid())
                .pieceProductUuid(vote.getPieceProductUuid())
                .build();
    }

    public ReadVoteListResponseVo toVo() {
        return ReadVoteListResponseVo.builder()
                .voteUuid(voteUuid)
                .productUuid(productUuid)
                .pieceProductUuid(pieceProductUuid)
                .build();
    }

}
