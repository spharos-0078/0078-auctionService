package com.pieceofcake.auction_service.bid.vo.out;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadBidResponseVo {
    private String bidUuid;
    private Long bidPrice;

    @Builder
    public ReadBidResponseVo(String bidUuid, Long bidPrice) {
        this.bidUuid = bidUuid;
        this.bidPrice = bidPrice;
    }
}
