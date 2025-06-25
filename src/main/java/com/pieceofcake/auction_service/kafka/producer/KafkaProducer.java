package com.pieceofcake.auction_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendVoteCloseEvent(String voteUuid) {
        kafkaTemplate.send("vote-close", voteUuid); // 단순히 voteUuid만 전송
    }

    public void sendAuctionCloseEvent(String auctionUuid) {
        kafkaTemplate.send("auction-close", auctionUuid);
    }
}