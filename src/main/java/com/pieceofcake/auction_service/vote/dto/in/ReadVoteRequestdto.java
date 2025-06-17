package com.pieceofcake.auction_service.vote.dto.in;

import com.pieceofcake.auction_service.vote.vo.in.ReadVoteRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadVoteRequestdto {
    private String productUuid;

    @Builder
    public ReadVoteRequestdto(String productUuid) {
        this.productUuid = productUuid;
    }

    public static ReadVoteRequestdto from(ReadVoteRequestVo readVoteRequestVo) {
        return ReadVoteRequestdto.builder()
                .productUuid(readVoteRequestVo.getProductUuid())
                .build();
    }
}
