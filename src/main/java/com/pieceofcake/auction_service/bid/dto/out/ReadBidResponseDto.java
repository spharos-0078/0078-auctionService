package com.pieceofcake.auction_service.bid.dto.out;

import com.pieceofcake.auction_service.bid.entity.Bid;
import com.pieceofcake.auction_service.bid.vo.out.ReadBidResponseVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadBidResponseDto {
    private String bidUuid;
    private Long bidPrice;

    @Builder
    public ReadBidResponseDto(String bidUuid, Long bidPrice) {
        this.bidUuid = bidUuid;
        this.bidPrice = bidPrice;
    }

    public static ReadBidResponseDto from(Bid bid) {
        return ReadBidResponseDto.builder()
                .bidUuid(bid.getBidUuid())
                .bidPrice(bid.getBidPrice())
                .build();
    }

    public ReadBidResponseVo toVo() {
        return ReadBidResponseVo.builder()
                .bidUuid(this.bidUuid)
                .bidPrice(this.bidPrice)
                .build();
    }
}
