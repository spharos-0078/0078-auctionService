package com.pieceofcake.auction_service.auction.infrastructure.client;

import com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.CreateMoneyRequestDto;
import com.pieceofcake.auction_service.auction.infrastructure.client.dto.in.CreateMoneyWithMemberUuidRequestDto;
import com.pieceofcake.auction_service.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-service",
        contextId = "AuctionFeignClient",
        url = "${payment-service.url}",
        configuration = FeignConfig.class)
public interface AuctionFeignClient {

    @PostMapping("/api/v1/money")
    void createMoney(@RequestBody CreateMoneyRequestDto createMoneyRequestDto);

    @PostMapping("/api/v1/money/with-member-uuid")
    void createMoneyWithMemberUuid(@RequestBody CreateMoneyWithMemberUuidRequestDto createMoneyWithMemberUuidRequestDto);
}
