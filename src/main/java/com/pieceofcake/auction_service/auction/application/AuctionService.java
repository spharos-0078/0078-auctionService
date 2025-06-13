package com.pieceofcake.auction_service.auction.application;

import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadHighestBidPriceResponseDto;

public interface AuctionService {
    ReadHighestBidPriceResponseDto readHighestBid(ReadHighestBidPriceRequestDto readHighestBidPriceRequestDto);
    void createAuction(CreateAuctionRequestDto createAuctionRequestDto);
    void updateAuction(UpdateAuctionDto updateAuctionDto);
}
