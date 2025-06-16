package com.pieceofcake.auction_service.bid.application;

import com.pieceofcake.auction_service.bid.dto.in.*;
import com.pieceofcake.auction_service.bid.dto.out.CreateBidResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadMyAuctionsResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadAllBidsByAuctionResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadBidResponseDto;

import java.util.List;

public interface BidService {
    CreateBidResponseDto createBid(CreateBidRequestDto createBidRequestDto);
    List<ReadBidResponseDto> readBids(ReadBidRequestDto readBidRequestDto);
    List<ReadMyAuctionsResponseDto> readMyAuctions(ReadMyAuctionsRequestDto readMyAuctionsRequestDto);
    List<ReadAllBidsByAuctionResponseDto> getBidsByAuctionUuid(
            ReadAllBidsByAuctionRequestDto readAllBidsByAuctionRequestDto);
    void hideBid(HideBidRequestDto hideBidRequestDto);
}
