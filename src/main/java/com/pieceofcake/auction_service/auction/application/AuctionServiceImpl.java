package com.pieceofcake.auction_service.auction.application;

import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadHighestBidPriceResponseDto;
import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.infrastructure.AuctionRepository;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService{

    private final AuctionRepository auctionRepository;

    @Override
    public ReadHighestBidPriceResponseDto readHighestBid(ReadHighestBidPriceRequestDto readHighestBidPriceRequestDto) {
        Auction auction = auctionRepository.findByAuctionUuid(readHighestBidPriceRequestDto.getAuctionUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AUCTION_NOT_FOUND));

        return ReadHighestBidPriceResponseDto.from(auction);
    }

}
