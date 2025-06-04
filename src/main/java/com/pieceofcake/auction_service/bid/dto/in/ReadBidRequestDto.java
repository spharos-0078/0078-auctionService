package com.pieceofcake.auction_service.bid.dto.in;

import com.pieceofcake.auction_service.bid.vo.in.ReadBidRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadBidRequestDto {
    private String memberUuid;
    private String auctionUuid;

    @Builder
    public ReadBidRequestDto(String memberUuid, String auctionUuid) {
        this.memberUuid = memberUuid;
        this.auctionUuid = auctionUuid;
    }

    public static ReadBidRequestDto of(String memberUuid, ReadBidRequestVo readBidRequestVo) {
        return ReadBidRequestDto.builder()
                .memberUuid(memberUuid)
                .auctionUuid(readBidRequestVo.getAuctionUuid())
                .build();
    }
}
