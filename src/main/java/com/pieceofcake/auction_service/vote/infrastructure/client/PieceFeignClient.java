package com.pieceofcake.auction_service.vote.infrastructure.client;

import com.pieceofcake.auction_service.common.config.FeignConfig;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.in.MemberPieceRequestDto;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseDto;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "piece-service",
        contextId = "piece-product-client", // 충돌 방지용 ID
        url = "${EC2_HOST}:8000/piece-service/api/v1",
        configuration = FeignConfig.class)
public interface PieceFeignClient {

    @GetMapping("/piece/owned/{pieceProductUuid}/list")
    MemberPieceResponseWrapper getMemberPieceQuantities(
            @PathVariable("pieceProductUuid") String pieceProductUuid
    );
}
