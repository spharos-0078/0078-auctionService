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
        url = "${EC2_HOST}:8000/payment-service/api/v1",
        configuration = FeignConfig.class)
public interface AuctionFeignClient {

    @PostMapping("/money")
    void createMoney(@RequestBody CreateMoneyRequestDto createMoneyRequestDto);

    @PostMapping("/money/with-member-uuid")
    void createMoneyWithMemberUuid(@RequestBody CreateMoneyWithMemberUuidRequestDto createMoneyWithMemberUuidRequestDto);
}
