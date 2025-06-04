package com.pieceofcake.auction_service.bid.application;

import com.pieceofcake.auction_service.bid.application.batch.BidQueueService;
import com.pieceofcake.auction_service.bid.dto.in.CreateBidRequestDto;
import com.pieceofcake.auction_service.bid.dto.in.ReadBidRequestDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadBidResponseDto;
import com.pieceofcake.auction_service.bid.entity.Bid;
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
    private final BidQueueService bidQueueService;

    @Override
    @Transactional
    public void createBid(CreateBidRequestDto createBidRequestDto) {
        Bid bid = createBidRequestDto.toEntity();
        // bid 저장
        bidRepository.save(bid);
        // 현재 1초 배치 주기에 처리되도록 큐에 추가
        bidQueueService.addBid(bid);
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
