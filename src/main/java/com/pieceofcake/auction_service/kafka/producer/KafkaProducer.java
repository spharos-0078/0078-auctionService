package com.pieceofcake.auction_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendVoteStartEvent(String productUuid) {
        kafkaTemplate.send("vote-start", productUuid); // 단순히 productUuid만 전송
    }

    public void sendVoteCloseEvent(String productUuid) {
        kafkaTemplate.send("vote-close", productUuid); // 단순히 voteUuid만 전송
    }

    public void sendAuctionStartEvent(String productUuid) {
        kafkaTemplate.send("auction-start", productUuid);
    }

    public void sendAuctionCloseEvent(String productUuid) {
        kafkaTemplate.send("auction-close", productUuid);
    }
}