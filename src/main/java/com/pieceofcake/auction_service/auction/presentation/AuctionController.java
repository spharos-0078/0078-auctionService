package com.pieceofcake.auction_service.auction.presentation;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadMyAuctionsRequestDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadMyAuctionsResponseDto;
import com.pieceofcake.auction_service.auction.vo.in.CreateAuctionRequestVo;
import com.pieceofcake.auction_service.auction.vo.in.ReadHighestBidPriceRequestVo;
import com.pieceofcake.auction_service.auction.vo.out.ReadHighestBidPriceResponseVo;
import com.pieceofcake.auction_service.auction.vo.out.ReadMyAuctionsResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/me/list")
    public List<ReadMyAuctionsResponseVo> getMyBidAuctions(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid
    ) {

        return auctionService.getMyBidAuctions(ReadMyAuctionsRequestDto.of(memberUuid))
                .stream()
                .map(ReadMyAuctionsResponseDto::toVo)
                .toList();
    }
}
