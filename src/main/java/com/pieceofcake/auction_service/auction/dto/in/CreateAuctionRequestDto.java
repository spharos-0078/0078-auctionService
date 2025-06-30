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
    private Long highestBidPrice;
    private String highestBidMemberUuid;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder
    public CreateAuctionRequestDto(
            String productUuid,
            String pieceProductUuid,
            Long startingPrice,
            Long highestBidPrice,
            String highestBidMemberUuid,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        this.productUuid = productUuid;
        this.pieceProductUuid = pieceProductUuid;
        this.startingPrice = startingPrice;
        this.highestBidPrice = highestBidPrice;
        this.highestBidMemberUuid = highestBidMemberUuid;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static CreateAuctionRequestDto from(CreateAuctionRequestVo createAuctionRequestVo) {
        return CreateAuctionRequestDto.builder()
                .pieceProductUuid(createAuctionRequestVo.getPieceProductUuid())
                .productUuid(createAuctionRequestVo.getProductUuid())
                .startingPrice(createAuctionRequestVo.getStartingPrice())
                .highestBidPrice(createAuctionRequestVo.getHighestBidPrice())
                .highestBidMemberUuid(createAuctionRequestVo.getHighestBidMemberUuid())
                .startDate(createAuctionRequestVo.getStartDate())
                .endDate(createAuctionRequestVo.getEndDate())
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
                .auctionStatus(AuctionStatus.ONGOING)
                .build();
    }
}
