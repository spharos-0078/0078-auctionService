package com.pieceofcake.auction_service.bid.dto.out;

import com.pieceofcake.auction_service.bid.vo.out.ReadMyAuctionsResponseVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadMyAuctionsResponseDto {
    private String auctionUuid;

    @Builder
    public ReadMyAuctionsResponseDto(String auctionUuid) {
        this.auctionUuid = auctionUuid;
    }

    public static ReadMyAuctionsResponseDto from(String auctionUuid) {
        return ReadMyAuctionsResponseDto.builder()
                .auctionUuid(auctionUuid)
                .build();
    }

    public ReadMyAuctionsResponseVo toVo() {
        return ReadMyAuctionsResponseVo.builder()
                .auctionUuid(this.auctionUuid)
                .build();
    }
}
