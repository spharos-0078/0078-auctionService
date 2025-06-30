package com.pieceofcake.auction_service.auction.application;

import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadAuctionListResponseDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadHighestBidPriceResponseDto;
import com.pieceofcake.auction_service.auction.entity.Auction;

import java.util.List;

public interface AuctionService {
    ReadHighestBidPriceResponseDto readHighestBid(ReadHighestBidPriceRequestDto readHighestBidPriceRequestDto);
    void createAuction(CreateAuctionRequestDto createAuctionRequestDto);
    void updateAuction(UpdateAuctionDto updateAuctionDto);
    void scheduleAuctionClose(Auction auction);
    void closeAuction(String auctionUuid);
    List<ReadAuctionListResponseDto> readAuctionList(String status);
}
