package com.pieceofcake.auction_service.auction.dto.in;

import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import com.pieceofcake.auction_service.auction.vo.in.CreateAuctionRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateAuctionRequestDto {
    private String productUuid;
    private Long startingPrice;
    private Long highestBidPrice;
    private String highestBidMemberUuid;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private AuctionStatus auctionStatus;

    @Builder
    public CreateAuctionRequestDto(String productUuid, Long startingPrice, Long highestBidPrice,
                                   String highestBidMemberUuid, LocalDateTime startTime, LocalDateTime endTime,
                                   AuctionStatus auctionStatus) {
        this.productUuid = productUuid;
        this.startingPrice = startingPrice;
        this.highestBidPrice = highestBidPrice;
        this.highestBidMemberUuid = highestBidMemberUuid;
        this.startDate = startTime;
        this.endDate = endTime;
        this.auctionStatus = auctionStatus;
    }

    public static CreateAuctionRequestDto from(CreateAuctionRequestVo createAuctionRequestVo) {
        return CreateAuctionRequestDto.builder()
                .productUuid(createAuctionRequestVo.getProductUuid())
                .startingPrice(createAuctionRequestVo.getStartingPrice())
                .highestBidPrice(createAuctionRequestVo.getHighestBidPrice())
                .highestBidMemberUuid(createAuctionRequestVo.getHighestBidMemberUuid())
                .startTime(createAuctionRequestVo.getStartDate())
                .endTime(createAuctionRequestVo.getEndDate())
                .auctionStatus(createAuctionRequestVo.getAuctionStatus())
                .build();
    }

    public Auction toEntity() {
        return Auction.builder()
                .auctionUuid(UUID.randomUUID().toString())
                .productUuid(this.productUuid)
                .startingPrice(this.startingPrice)
                .highestBidPrice(this.highestBidPrice)
                .highestBidMemberUuid(this.highestBidMemberUuid)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .auctionStatus(this.auctionStatus)
                .build();
    }
}
