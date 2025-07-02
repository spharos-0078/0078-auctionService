package com.pieceofcake.auction_service.auction.presentation;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.application.sse.AuctionEventService;
import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadAuctionListResponseDto;
import com.pieceofcake.auction_service.auction.dto.out.UpdateAuctionPriceSseDto;
import com.pieceofcake.auction_service.auction.vo.in.CreateAuctionRequestVo;
import com.pieceofcake.auction_service.auction.vo.in.ReadHighestBidPriceRequestVo;
import com.pieceofcake.auction_service.auction.vo.out.ReadAuctionListResponseVo;
import com.pieceofcake.auction_service.auction.vo.out.ReadHighestBidPriceResponseVo;
import com.pieceofcake.auction_service.common.entity.BaseResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionEventService auctionEventService;

    @Operation(
            summary = "최고 입찰가 조회 API",
            description = "특정 경매의 현재 최고 입찰가를 조회하는 API입니다.\n\n" +
                    "- 경로 변수로 경매 UUID를 받아서 해당 경매의 최고 입찰가 정보를 반환합니다."
    )
    @GetMapping("/highest-price/{auctionUuid}")
    public ReadHighestBidPriceResponseVo getHighestPrice(
            @PathVariable("auctionUuid") 
            @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
                     message = "올바른 UUID 형식이 아닙니다") 
            String auctionUuid
    ) {
        ReadHighestBidPriceRequestVo readHighestBidPriceRequestVo = ReadHighestBidPriceRequestVo.builder()
                .auctionUuid(auctionUuid)
                .build();

        return auctionService.readHighestBid(ReadHighestBidPriceRequestDto.from(readHighestBidPriceRequestVo)).toVo();
    }

    @Operation(
            summary = "경매 생성 API",
            description = "새로운 경매를 생성하는 API입니다.\n\n" +
                    "- 사실상 쓰지 않을 api입니다. (투표 종료 즉시 결과에 따라 서비스 딴에서 auction을 만들 예정입니다\n\n" +
                    "- 요청 본문에 `CreateAuctionRequestVo` 객체를 포함해야 합니다.\n\n" +
                    "- 경매 생성 시, productUuid, startingPrice 등을 포함합니다.\n\n" +
                    "- highestBidPrice와 highestBidMemberUuid는 null로 해도 됩니다. 정책상 차후 '구매희망자 등장 => 경매진행투표 => 경매 찬성, 이후 구매희망자를 강제로 최초입찰자로'.\n\n" +
                    "  - 위와 같은 로직이 될 수도 있나? 해서 넣어본 겁니다."
    )
    @PostMapping("")
    public void createAuction(
            @RequestBody @Valid CreateAuctionRequestVo createAuctionRequestVo
    ) {
        auctionService.createAuction(CreateAuctionRequestDto.from(createAuctionRequestVo));
    }

    @Operation(
            summary = "경매 최고가 조회 SSE API",
            description = "Server-Sent Events를 사용하여 실시간으로 경매 가격 업데이트를 스트리밍하는 API입니다.\n\n" +
                    "- path variable로 경매 UUID를 받아 해당 경매의 가격 업데이트 이벤트를 실시간으로 제공합니다.\n" +
                    "- 클라이언트는 이 엔드포인트에 연결하여 가격 변동을 실시간으로 모니터링할 수 있습니다.\n\n" +
                    "- 위의 '최고가 조회 api'는 단건 조회용, 이 api는 SSE로 갱신하는 용."
    )
    @GetMapping(value = "/sse/price-updates/{auctionUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<UpdateAuctionPriceSseDto>> streamAuctionPriceUpdates(
            @PathVariable("auctionUuid") String auctionUuid) {
        return auctionEventService.getAuctionPriceUpdatesByAuctionId(auctionUuid)
                .map(event -> ServerSentEvent.<UpdateAuctionPriceSseDto>builder()
                        .event("price-update")
                        .data(event)
                        .build());
    }

    @GetMapping("/list")
    public BaseResponseEntity<List<ReadAuctionListResponseVo>> getAuctionList(
            @RequestParam(required = false) String status
    ) {
        return new BaseResponseEntity<>(auctionService.readAuctionList(status)
                .stream()
                .map(ReadAuctionListResponseDto::toVo)
                .toList());
    }

}
