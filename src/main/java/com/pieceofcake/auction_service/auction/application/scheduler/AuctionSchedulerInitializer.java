package com.pieceofcake.auction_service.auction.application.scheduler;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.entity.Auction;
import com.pieceofcake.auction_service.auction.entity.enums.AuctionStatus;
import com.pieceofcake.auction_service.auction.infrastructure.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuctionSchedulerInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final AuctionRepository auctionRepository;
    private final AuctionService auctionService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<Auction> activeAuctions = auctionRepository.findAllByAuctionStatusAndEndDateAfter(
                AuctionStatus.ONGOING, LocalDateTime.now());

        for (Auction auction : activeAuctions) {
            auctionService.scheduleAuctionClose(auction);
        }
    }

}
