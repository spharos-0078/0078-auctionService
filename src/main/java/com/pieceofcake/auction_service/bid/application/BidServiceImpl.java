package com.pieceofcake.auction_service.bid.application;

import com.pieceofcake.auction_service.bid.dto.in.CreateBidRequestDto;
import com.pieceofcake.auction_service.bid.dto.in.ReadBidRequestDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadBidResponseDto;
import com.pieceofcake.auction_service.bid.infrastructure.BidRepository;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService{
    private final BidRepository bidRepository;

    @Override
    @Transactional
    public void createBid(CreateBidRequestDto createBidRequestDto) {
        bidRepository.save(createBidRequestDto.toEntity());
    }

    @Override
    public ReadBidResponseDto readBid(ReadBidRequestDto readBidRequestDto) {
        return ReadBidResponseDto.from(bidRepository.findByAuctionUuidAndMemberUuidAndDeletedFalse(
                readBidRequestDto.getAuctionUuid(),
                readBidRequestDto.getMemberUuid()
            )
            .orElseThrow(() -> new BaseException(BaseResponseStatus.BID_NOT_FOUND))
        );
    }


}
