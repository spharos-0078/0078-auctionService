package com.pieceofcake.auction_service.bid.presentation;

import com.pieceofcake.auction_service.bid.application.BidService;
import com.pieceofcake.auction_service.bid.dto.in.CreateBidRequestDto;
import com.pieceofcake.auction_service.bid.vo.in.CreateBidRequestVo;
import com.pieceofcake.auction_service.common.entity.BaseResponseEntity;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bid")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping("")
    public BaseResponseEntity<Void> createBid(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody CreateBidRequestVo createBidRequestVo
    ){
        bidService.createBid(CreateBidRequestDto.of(memberUuid, createBidRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }
}
