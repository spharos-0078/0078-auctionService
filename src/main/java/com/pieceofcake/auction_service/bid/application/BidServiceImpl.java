package com.pieceofcake.auction_service.bid.application;

import com.pieceofcake.auction_service.bid.dto.in.CreateBidRequestDto;
import com.pieceofcake.auction_service.bid.infrastructure.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService{
    private final BidRepository bidRepository;

    public void createBid(CreateBidRequestDto createBidRequestDto) {
        bidRepository.save(createBidRequestDto.toEntity());
    }
}
