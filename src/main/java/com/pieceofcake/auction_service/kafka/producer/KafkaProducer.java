package com.pieceofcake.auction_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendVoteStartEvent(String pieceProductUuid) {
        kafkaTemplate.send("vote-start", pieceProductUuid); // 단순히 productUuid만 전송
    }

    public void sendVoteCloseEvent(String pieceProductUuid) {
        kafkaTemplate.send("vote-close", pieceProductUuid); // 단순히 voteUuid만 전송
    }

    public void sendAuctionStartEvent(String pieceProductUuid) {
        kafkaTemplate.send("auction-start", pieceProductUuid);
    }

    public void sendAuctionCloseEvent(String pieceProductUuid) {
        kafkaTemplate.send("auction-close", pieceProductUuid);
    }
}