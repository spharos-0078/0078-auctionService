package com.pieceofcake.auction_service.bid.application;

import com.pieceofcake.auction_service.bid.dto.in.ReadMyAuctionsRequestDto;
import com.pieceofcake.auction_service.bid.dto.out.CreateBidResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadMyAuctionsResponseDto;
import com.pieceofcake.auction_service.bid.dto.in.CreateBidRequestDto;
import com.pieceofcake.auction_service.bid.dto.in.ReadAllBidsByAuctionRequestDto;
import com.pieceofcake.auction_service.bid.dto.in.ReadBidRequestDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadAllBidsByAuctionResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadBidResponseDto;

import java.util.List;

public interface BidService {
    CreateBidResponseDto createBid(CreateBidRequestDto createBidRequestDto);
    ReadBidResponseDto readBid(ReadBidRequestDto readBidRequestDto);
    List<ReadMyAuctionsResponseDto> readMyAuctions(ReadMyAuctionsRequestDto readMyAuctionsRequestDto);
    List<ReadAllBidsByAuctionResponseDto> getBidsByAuctionUuid(
            ReadAllBidsByAuctionRequestDto readAllBidsByAuctionRequestDto);
}
