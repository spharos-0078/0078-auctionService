package com.pieceofcake.auction_service.bid.infrastructure.client;

import com.pieceofcake.auction_service.bid.infrastructure.client.dto.out.ReadRemainingMoneyResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "payment-service",
        url = "${payment-service.url}")
public interface BidFeignClient {

    @GetMapping("/api/v1/money")
    ReadRemainingMoneyResponseWrapper getRemainingMoney();

}
