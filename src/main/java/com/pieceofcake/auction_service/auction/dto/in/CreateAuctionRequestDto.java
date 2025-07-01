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
    private String pieceProductUuid;
    private Long startingPrice;
    private String highestBidUuid;
    private Long highestBidPrice;
    private String highestBidMemberUuid;
    private AuctionStatus auctionStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder
    public CreateAuctionRequestDto(
            String productUuid,
            String pieceProductUuid,
            Long startingPrice,
            String highestBidUuid,
            Long highestBidPrice,
            String highestBidMemberUuid,
            AuctionStatus auctionStatus,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        this.productUuid = productUuid;
        this.pieceProductUuid = pieceProductUuid;
        this.startingPrice = startingPrice;
        this.highestBidUuid = highestBidUuid;
        this.highestBidPrice = highestBidPrice;
        this.highestBidMemberUuid = highestBidMemberUuid;
        this.auctionStatus = auctionStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static CreateAuctionRequestDto from(CreateAuctionRequestVo createAuctionRequestVo) {
        return CreateAuctionRequestDto.builder()
                .productUuid(createAuctionRequestVo.getProductUuid())
                .pieceProductUuid(createAuctionRequestVo.getPieceProductUuid())
                .startingPrice(createAuctionRequestVo.getStartingPrice())
                .highestBidUuid(createAuctionRequestVo.getHighestBidUuid())
                .highestBidPrice(createAuctionRequestVo.getHighestBidPrice())
                .highestBidMemberUuid(createAuctionRequestVo.getHighestBidMemberUuid())
                .startDate(createAuctionRequestVo.getStartDate())
                .endDate(createAuctionRequestVo.getEndDate())
                .auctionStatus(createAuctionRequestVo.getAuctionStatus())
                .build();
    }

    public Auction toEntity() {
        return Auction.builder()
                .auctionUuid(UUID.randomUUID().toString())
                .productUuid(this.productUuid)
                .pieceProductUuid(this.pieceProductUuid)
                .startingPrice(this.startingPrice)
                .highestBidUuid(this.highestBidUuid)
                .highestBidPrice(this.highestBidPrice)
                .highestBidMemberUuid(this.highestBidMemberUuid)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .auctionStatus(this.auctionStatus)
                .build();
    }
}
