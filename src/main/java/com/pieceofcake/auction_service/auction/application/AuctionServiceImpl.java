package com.pieceofcake.auction_service.auction.application;

import com.pieceofcake.auction_service.auction.dto.in.CreateAuctionRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadHighestBidPriceRequestDto;
import com.pieceofcake.auction_service.auction.dto.in.ReadMyAuctionsRequestDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadHighestBidPriceResponseDto;
import com.pieceofcake.auction_service.auction.dto.out.ReadMyAuctionsResponseDto;
import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.infrastructure.AuctionRepository;
import com.pieceofcake.auction_service.bid.application.BidService;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService{

    private final AuctionRepository auctionRepository;
    private final BidService bidService;

    @Override
    public ReadHighestBidPriceResponseDto readHighestBid(ReadHighestBidPriceRequestDto readHighestBidPriceRequestDto) {
        Auction auction = auctionRepository.findByAuctionUuid(readHighestBidPriceRequestDto.getAuctionUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AUCTION_NOT_FOUND));

        return ReadHighestBidPriceResponseDto.from(auction);
    }

    @Override
    @Transactional
    public void createAuction(CreateAuctionRequestDto createAuctionRequestDto) {
        Auction auction = createAuctionRequestDto.toEntity();
        auctionRepository.save(auction);
    }

    @Override
    public List<ReadMyAuctionsResponseDto> getMyBidAuctions(ReadMyAuctionsRequestDto readMyAuctionsRequestDto) {

        List<ReadMyAuctionsResponseDto> result = bidService.readMyAuctions(readMyAuctionsRequestDto);
        return result;
    }
}
