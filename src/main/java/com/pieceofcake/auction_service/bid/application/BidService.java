package com.pieceofcake.auction_service.bid.application;

import com.pieceofcake.auction_service.bid.dto.in.CreateBidRequestDto;

public interface BidService {
    void createBid(CreateBidRequestDto createBidRequestDto);
}
