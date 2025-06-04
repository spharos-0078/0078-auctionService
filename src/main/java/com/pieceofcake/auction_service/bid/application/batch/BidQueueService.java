package com.pieceofcake.auction_service.bid.application.batch;

import com.pieceofcake.auction_service.bid.entity.Bid;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BidQueueService {
    // 1초 동안 누적된 입찰을 모아서 처리하기 위한 큐 서비스

    // 스레드 안전한 BlockingQueue 사용 (LinkedBlockingQueue는 FIFO, 다중 쓰레드 환경 안전)
    private final BlockingQueue<Bid> bidQueue = new LinkedBlockingQueue<>();

    // 새로운 입찰 추가 (Producer들이 호출)
    public void addBid(Bid bid) {
        bidQueue.offer(bid);
    }

    // 누적된 입찰 모두 가져오기 (Consumer가 1초마다 호출)
    public List<Bid> drainAll() {
        List<Bid> batchList = new ArrayList<>();
        bidQueue.drainTo(batchList);
        return batchList;
    }

}