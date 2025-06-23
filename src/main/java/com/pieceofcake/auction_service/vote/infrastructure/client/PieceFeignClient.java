package com.pieceofcake.auction_service.vote.infrastructure.client;

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
        url = "${payment-service.url}")
public interface PieceFeignClient {

    @GetMapping("/api/v1/piece/owned/{productUuid}/list")
    MemberPieceResponseWrapper getMemberPieceQuantities(
            @PathVariable("productUuid") String productUuid
    );
}
