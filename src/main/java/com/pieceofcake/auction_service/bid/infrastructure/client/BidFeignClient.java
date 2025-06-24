package com.pieceofcake.auction_service.bid.infrastructure.client;

import com.pieceofcake.auction_service.bid.infrastructure.client.dto.out.ReadRemainingMoneyResponseWrapper;
import com.pieceofcake.auction_service.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "payment-service",
        contextId = "bidFeignClient",
        url = "${payment-service.url}",
        configuration = FeignConfig.class)
public interface BidFeignClient {

    @GetMapping("/api/v1/money")
    ReadRemainingMoneyResponseWrapper getRemainingMoney();

}
