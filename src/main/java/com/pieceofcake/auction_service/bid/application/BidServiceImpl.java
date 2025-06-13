package com.pieceofcake.auction_service.bid.application;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.dto.in.UpdateAuctionDto;
import com.pieceofcake.auction_service.bid.dto.in.ReadMyAuctionsRequestDto;
import com.pieceofcake.auction_service.bid.dto.out.CreateBidResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadMyAuctionsResponseDto;
import com.pieceofcake.auction_service.bid.dto.in.CreateBidRequestDto;
import com.pieceofcake.auction_service.bid.dto.in.ReadAllBidsByAuctionRequestDto;
import com.pieceofcake.auction_service.bid.dto.in.ReadBidRequestDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadAllBidsByAuctionResponseDto;
import com.pieceofcake.auction_service.bid.dto.out.ReadBidResponseDto;
import com.pieceofcake.auction_service.bid.entity.Bid;
import com.pieceofcake.auction_service.bid.infrastructure.BidRepository;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.common.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    @Override
    @Transactional
    public CreateBidResponseDto createBid(CreateBidRequestDto createBidRequestDto) {
        Bid bid = createBidRequestDto.toEntity();
        String auctionUuid = bid.getAuctionUuid();
        Long currentTimestamp = System.currentTimeMillis();

        // 1. redis 최고가와 비교
        String redisHighestBidKey = "auction:highestBid:" + auctionUuid;
        Map<Object, Object> redisHighestBidMap = redisTemplate.opsForHash().entries(redisHighestBidKey);
        Long highestBidPrice = redisHighestBidMap.get("bidPrice") != null
                ? Long.parseLong((String) redisHighestBidMap.get("bidPrice"))
                : 0L;
        Long lastUpdateTimestamp = redisHighestBidMap.get("timestamp") != null
                ? Long.parseLong((String) redisHighestBidMap.get("timestamp"))
                : 0L;

        // 2. 최고가가 아니면, bid SQL에 저장, 유저에게 입찰 실패 로직 전달
        //    지금은 대응되는 auctionUuid 없어도 일단 bid SQL에 저장.
        //    무결성 깨질 수 있나?
        if (bid.getBidPrice() <= highestBidPrice) {
            bidRepository.save(bid);
            return CreateBidResponseDto.builder()
                    .success(false)
                    .message("입찰 실패")
                    .build();
        }

        // 3. 최고가면, redis에 최고가 갈아끼우기
        Map<String, String> bidData = new HashMap<>();
        bidData.put("bidPrice", String.valueOf(bid.getBidPrice()));
        bidData.put("bidUuid", bid.getBidUuid());
        bidData.put("bidMemberUuid", bid.getMemberUuid());
        bidData.put("timestamp", String.valueOf(currentTimestamp));
        redisTemplate.opsForHash().putAll(redisHighestBidKey, bidData);

        redisTemplate.expire(redisHighestBidKey, Duration.ofDays(14));  // 2주 간 저장

        // 4. 경매 상태 업데이트.
        //    트래픽 판단 후, 분기처리해서 스케쥴러 turn on/off
        //    요청량 많으면 redis에 최고가 저장하고, 경매 상태 업데이트는 묶음(batch)로 처리.
        //    이 때 이전 최고가 timestamp와 현재 최고가 timestamp 간격을 비교, 짧으면 batch 처리 + scheduler 사용해서 경매 sql 업데이트
        //    이후 bid SQL에 저장

        // 현재 flag의 남은 만료 시간 확인 (밀리초 단위)
        Long remainingTTL = redisTemplate.getExpire("auction:useScheduler:" + auctionUuid, TimeUnit.MILLISECONDS);

        if (currentTimestamp - lastUpdateTimestamp < 100) {
            // 트래픽이 많음
            // 1. flag 있고 TTL 많으면 넘어감
            // 2. flag 있고 TTL 적으면 갱신
            // 3. flag 없으면 생성

            // flag 상태 확인
            if (remainingTTL == null) {
                // Redis 연결 오류 등 (일반적으로 발생하지 않음)
                log.warn("Redis getExpire returned null for key: auction:useScheduler:{}", auctionUuid);
            } else if (remainingTTL == -2) {
                // 3. flag가 없는 경우: 새로 생성
                redisTemplate.opsForValue().set("auction:useScheduler:" + auctionUuid, "true", Duration.ofSeconds(2));
            } else if (remainingTTL == -1) {
                // flag가 영구적인 경우 (일반적으로는 발생하지 않음)
                log.warn("Found permanent flag for auction:useScheduler:{}", auctionUuid);
            } else if (remainingTTL <= 500) {
                // 2. flag가 있고 TTL이 1초 이하로 적은 경우: 갱신
                redisTemplate.opsForValue().set("auction:useScheduler:" + auctionUuid, "true", Duration.ofSeconds(2));
            }

        } else if (remainingTTL == -2){
            // 트래픽이 적고, flag 없으면, 바로 경매 상태 업데이트
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
    public ReadBidResponseDto readBid(ReadBidRequestDto readBidRequestDto) {
        return ReadBidResponseDto.from(bidRepository.findByAuctionUuidAndMemberUuidAndDeletedFalse(
                readBidRequestDto.getAuctionUuid(),
                readBidRequestDto.getMemberUuid()
            )
            .orElseThrow(() -> new BaseException(BaseResponseStatus.BID_NOT_FOUND))
        );
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
}
