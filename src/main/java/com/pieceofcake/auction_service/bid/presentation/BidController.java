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
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bid")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;


    @Operation(
            summary = "입찰 생성 API",
            description = "새로운 입찰을 생성하는 API입니다.\n\n" +
                    "- 요청 바디에는 입찰 정보를 포함합니다.\n" +
                    "- 많은 서비스와 연결되어 있습니다.\n" +
                    "  - 입찰 생성 api 요청 => 'redis' 최고가 조회 => 'money' 예치금 조회 => 'redis' 최고가 갱신\n" +
                    "  - => 'redis' 분기처리로 트래픽 많은지 조회 => 'auction' 경매 수정 => 'money' 기존 입찰자의 보증금 반환\n" +
                    "  - => 'money' 신규 입찰자의 보증금 책정' => 'redis' SSE 이벤트 발행 => bid 생성\n" +
                    "- feign client에서 에러 처리가 나오면, 그 feign 요청 시의 에러 메시지를 반환할 수 있습니다."
    )
    @PostMapping("")
    public BaseResponseEntity<CreateBidResponseVo> createBid(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody CreateBidRequestVo createBidRequestVo
    ){
        CreateBidResponseVo result = bidService.createBid(
                CreateBidRequestDto.of(memberUuid, createBidRequestVo)).toVo();
        return new BaseResponseEntity<>(result);
    }

    @Operation(
            summary = "나의 입찰 가격 조회 API",
            description = "특정 경매에 대한 입찰(bid) 리스트를 조회하는 API입니다.\n\n" +
                    "- path variable로 auctionUuid를 받아옵니다.\n" +
                    "- JWT로 memberUuid를 받아옵니다. (내 입찰내역만 볼 수 있습니다.)\n" +
                    "- response는 해당 경매에 대한 {bidUuid, bidPrice}[] 를 반환합니다."
    )
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

    @Operation(
            summary = "경매에 대한 모든 입찰 조회 API",
            description = "특정 경매(auction)에 대한 모든 입찰(bid) 리스트를 조회하는 API입니다.\n\n" +
                    "- path variable로 auctionUuid를 받아옵니다.\n" +
                    "- 해당 경매에 대한 모든 입찰 정보를 반환합니다.\n" +
                    "- response는 {bidUuid, bidPrice, memberUuid}[] 형태로 반환됩니다.\n"
    )
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

    @Operation(
            summary = "내가 입찰한 경매 리스트 조회 API",
            description = "내가 참여한 입찰(bid)에 대한 경매(auction) 리스트를 조회하는 API입니다.\n\n" +
                    "- JWT로 memberUuid를 받아옵니다.\n" +
                    "- response는 내가 참여한 경매에 대한 {auctionUuid}[] 를 반환합니다."
    )
    @GetMapping("/me/auctions")
    public BaseResponseEntity<List<ReadMyAuctionsResponseVo>> getMyBidAuctions(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid
    ) {

        return new BaseResponseEntity<>(bidService.readMyAuctions(ReadMyAuctionsRequestDto.of(memberUuid))
                .stream()
                .map(ReadMyAuctionsResponseDto::toVo)
                .toList());
    }

    @Operation(
            summary = "입찰(bid) 숨김 요청 API",
            description = "특정 입찰(bid)의 hidden 컬럼을 true로 변경하는 API입니다.\n\n" +
                    "- path variable로 bidUuid를 받아옵니다.\n" +
                    "- 해당 입찰은 이후 mypage에서 내가 특정 경매에 한 입찰을 조회할 때 나오지 않습니다.\n" +
                    "- 다른 조회에서는 나올 수 있습니다 (특정 경매의 모든 입찰 내역 출력 시).\n"
    )
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
