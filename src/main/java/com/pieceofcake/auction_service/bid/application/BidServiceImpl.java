package com.pieceofcake.auction_service.bid.application;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import com.pieceofcake.auction_service.auction.infrastructure.AuctionRepository;
import com.pieceofcake.auction_service.bid.dto.in.*;
import com.pieceofcake.auction_service.bid.dto.out.CreateBidResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadMyAuctionsResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadAllBidsByAuctionResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadBidResponseDto;
import com.pieceofcake.auction_service.bid.entity.Bid;
import com.pieceofcake.auction_service.bid.infrastructure.BidRepository;
import com.pieceofcake.auction_service.bid.infrastructure.client.BidFeignClient;
import com.pieceofcake.auction_service.bid.infrastructure.client.dto.out.ReadRemainingMoneyResponseDto;
import com.pieceofcake.auction_service.bid.infrastructure.client.dto.out.ReadRemainingMoneyResponseWrapper;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseDto;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseWrapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService{
    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final StringRedisTemplate redisTemplate;
    private final AuctionRepository auctionRepository;
    private final BidFeignClient bidFeignClient;
    private final AtomicBidService atomicBidService;

    @Override
    @Transactional
    public CreateBidResponseDto createBid(CreateBidRequestDto createBidRequestDto) {
        Bid bid = createBidRequestDto.toEntity();
        String auctionUuid = bid.getAuctionUuid();

        // 0. auction 존재하는지 확인
        Auction auction = auctionRepository.findByAuctionUuid(createBidRequestDto.getAuctionUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.AUCTION_NOT_FOUND));

        // 1. 경매 상태가 진행 중인지 확인
        LocalDateTime now = LocalDateTime.now();
        if (auction.getAuctionStatus() != AuctionStatus.ONGOING || now.isAfter(auction.getEndDate())) {
            return CreateBidResponseDto.builder()
                    .success(false)
                    .message("경매 마감")
                    .build();
        }

        // 2. 예치금 확인 (원자적 처리 전에 먼저 확인)
        ReadRemainingMoneyResponseWrapper response = bidFeignClient.getRemainingMoney();
        ReadRemainingMoneyResponseDto remainingMoneyDto = response.getResult();

        if(bid.getBidPrice() > remainingMoneyDto.getAmount()) {
            return CreateBidResponseDto.builder()
                    .success(false)
                    .message("예치금 부족")
                    .build();
        }

        // 3. 원자적 입찰 처리
        boolean bidSuccess = atomicBidService.processBidAtomically(auctionUuid, bid);
        
        if (!bidSuccess) {
            // 입찰 실패 - 최고가가 아니거나 락 획득 실패
            bidRepository.save(bid);
            return CreateBidResponseDto.builder()
                    .success(false)
                    .message("입찰 실패 - 더 높은 입찰가가 존재합니다")
                    .build();
        }

        // 4. 성공한 입찰에 대해서만 경매 상태 업데이트 처리
        AtomicBidService.BidInfo currentBid = atomicBidService.getCurrentHighestBid(auctionUuid);
        
        // 트래픽에 따른 배치 처리 결정
        Long remainingTTL = redisTemplate.getExpire("auction:useScheduler:" + auctionUuid, TimeUnit.MILLISECONDS);
        
        if (currentBid.getTimestamp() - System.currentTimeMillis() < 1000) {
            // 트래픽이 많음 - 배치 처리 사용
            if (remainingTTL == null || remainingTTL == -2) {
                redisTemplate.opsForValue().set("auction:useScheduler:" + auctionUuid, "true", Duration.ofSeconds(2));
            } else if (remainingTTL <= 1100) {
                redisTemplate.opsForValue().set("auction:useScheduler:" + auctionUuid, "true", Duration.ofSeconds(2));
            }
        } else if (remainingTTL == -2) {
            // 트래픽이 적음 - 즉시 처리
            auctionService.updateAuction(UpdateAuctionDto.builder()
                    .auctionUuid(auctionUuid)
                    .bidUuid(bid.getBidUuid())
                    .bidPrice(bid.getBidPrice())
                    .memberUuid(bid.getMemberUuid())
                    .build());
        }

        bidRepository.save(bid);

        return CreateBidResponseDto.builder()
                .success(true)
                .message("입찰 성공")
                .build();
    }

    @Override
    public List<ReadBidResponseDto> readBids(ReadBidRequestDto readBidRequestDto) {

        List<Bid> bids = bidRepository.findByAuctionUuidAndMemberUuidAndDeletedFalseAndHiddenFalseOrderByCreatedAtDesc(
                readBidRequestDto.getAuctionUuid(),
                readBidRequestDto.getMemberUuid()
        );

        return bids.stream()
                .map(ReadBidResponseDto::from)
                .toList();
    }

    @Override
    public List<ReadMyAuctionsResponseDto> readMyAuctions(ReadMyAuctionsRequestDto readMyAuctionsRequestDto) {
        List<String> bids = bidRepository.findDistinctAuctionUuidsByMemberUuid(
                readMyAuctionsRequestDto.getMemberUuid());

        return bids
                .stream()
                .map(ReadMyAuctionsResponseDto::from)
                .toList();
    }

    @Override
    public List<ReadAllBidsByAuctionResponseDto> getBidsByAuctionUuid(
            ReadAllBidsByAuctionRequestDto readAllBidsByAuctionRequestDto
    ) {
        return bidRepository.findAllByAuctionUuidAndDeletedFalse(readAllBidsByAuctionRequestDto.getAuctionUuid())
                .stream()
                .map(ReadAllBidsByAuctionResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public void hideBid(HideBidRequestDto hideBidRequestDto) {
        Bid bid = bidRepository.findByBidUuidAndMemberUuidAndDeletedFalse(
                hideBidRequestDto.getBidUuid(),
                hideBidRequestDto.getMemberUuid()
        ).orElseThrow(() -> new BaseException(BaseResponseStatus.BID_NOT_FOUND));

        // 입찰 숨기기
        bidRepository.save(hideBidRequestDto.udpateEntity(bid));

    }
}
