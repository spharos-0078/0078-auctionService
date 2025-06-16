package com.pieceofcake.auction_service.bid.vo.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HideBidRequestVo {
    private String bidUuid;
    private String memberUuid;

    @Builder
    public HideBidRequestVo(String bidUuid, String memberUuid) {
        this.bidUuid = bidUuid;
        this.memberUuid = memberUuid;
    }
}
