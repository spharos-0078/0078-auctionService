package com.pieceofcake.auction_service.bid.presentation;

import com.pieceofcake.auction_service.bid.application.BidService;
import com.pieceofcake.auction_service.bid.dto.in.*;
import com.pieceofcake.auction_service.bid.dto.out.ReadAllBidsByAuctionResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadBidResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadMyAuctionsResponseDto;
import com.pieceofcake.auction_service.bid.vo.in.CreateBidRequestVo;
import com.pieceofcake.auction_service.bid.vo.in.HideBidRequestVo;
import com.pieceofcake.auction_service.bid.vo.in.ReadAllBidsByAuctionRequestVo;
import com.pieceofcake.auction_service.bid.vo.in.ReadBidRequestVo;
import com.pieceofcake.auction_service.bid.vo.out.CreateBidResponseVo;
import com.pieceofcake.auction_service.bid.vo.out.ReadAllBidsByAuctionResponseVo;
import com.pieceofcake.auction_service.bid.vo.out.ReadBidResponseVo;
import com.pieceofcake.auction_service.bid.vo.out.ReadMyAuctionsResponseVo;
import com.pieceofcake.auction_service.common.entity.BaseResponseEntity;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bid")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping("")
    public BaseResponseEntity<CreateBidResponseVo> createBid(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody CreateBidRequestVo createBidRequestVo
    ){
        CreateBidResponseVo result = bidService.createBid(
                CreateBidRequestDto.of(memberUuid, createBidRequestVo)).toVo();
        return new BaseResponseEntity<>(result);
    }

    @GetMapping("/me/{auctionUuid}")
    public BaseResponseEntity<List<ReadBidResponseVo>> getMyBidPrice(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @ModelAttribute ReadBidRequestVo readBidRequestVo
    ) {
        return new BaseResponseEntity<>(bidService.readBids(ReadBidRequestDto.of(memberUuid, readBidRequestVo))
                .stream()
                .map(ReadBidResponseDto::toVo)
                .toList());
    }

    @GetMapping("/list/{auctionUuid}")
    public BaseResponseEntity<List<ReadAllBidsByAuctionResponseVo>> getAllBidsByAuction(
            @PathVariable(value = "auctionUuid") String auctionUuid
    ) {
        ReadAllBidsByAuctionRequestVo readAllBidsByAuctionRequestVo = ReadAllBidsByAuctionRequestVo.builder()
                .auctionUuid(auctionUuid)
                .build();

        return new BaseResponseEntity<>(
                bidService.getBidsByAuctionUuid(ReadAllBidsByAuctionRequestDto.from(readAllBidsByAuctionRequestVo))
                        .stream()
                        .map(ReadAllBidsByAuctionResponseDto::toVo)
                        .toList()
        );
    }

    @GetMapping("/me/auctions")
    public List<ReadMyAuctionsResponseVo> getMyBidAuctions(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid
    ) {

        return bidService.readMyAuctions(ReadMyAuctionsRequestDto.of(memberUuid))
                .stream()
                .map(ReadMyAuctionsResponseDto::toVo)
                .toList();
    }

    @PostMapping("/me/hide/{bidUuid}")
    public BaseResponseEntity<Void> hideMyBid(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @PathVariable("bidUuid") String bidUuid
    ) {
        HideBidRequestVo hideBidRequestVo = HideBidRequestVo.builder()
                .memberUuid(memberUuid)
                .bidUuid(bidUuid)
                .build();

        bidService.hideBid(HideBidRequestDto.from(hideBidRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }
}
