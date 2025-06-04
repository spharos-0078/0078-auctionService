package com.pieceofcake.auction_service.auction.presentation;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.vo.in.CreateAuctionRequestVo;
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
            @PathVariable("auctionUuid") String auctionUuid
    ) {
        ReadHighestBidPriceRequestVo readHighestBidPriceRequestVo = ReadHighestBidPriceRequestVo.builder()
                .auctionUuid(auctionUuid)
                .build();

        return auctionService.readHighestBid(ReadHighestBidPriceRequestDto.from(readHighestBidPriceRequestVo)).toVo();
    }

    @PostMapping("")
    public void createAuction(
            @RequestBody CreateAuctionRequestVo createAuctionRequestVo
    ) {
        auctionService.createAuction(CreateAuctionRequestDto.from(createAuctionRequestVo));
    }
}
