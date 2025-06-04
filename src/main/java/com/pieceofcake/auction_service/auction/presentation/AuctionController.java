package com.pieceofcake.auction_service.auction.presentation;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.vo.in.ReadHighestBidPriceRequestVo;
import com.pieceofcake.auction_service.auction.vo.out.ReadHighestBidPriceResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping("/highest-price/{auctionUuid}")
    public ReadHighestBidPriceResponseVo getHighestPrice(
            @RequestParam(value = "auctionUuid") String auctionUuid
    ) {
        ReadHighestBidPriceRequestVo readHighestBidPriceRequestVo = ReadHighestBidPriceRequestVo.builder()
                .auctionUuid(auctionUuid)
                .build();
        
        return auctionService.readHighestBid(ReadHighestBidPriceRequestDto.from(readHighestBidPriceRequestVo)).toVo();
    }
}
