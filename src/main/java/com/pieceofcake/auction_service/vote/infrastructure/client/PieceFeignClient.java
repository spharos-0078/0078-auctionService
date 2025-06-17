package com.pieceofcake.auction_service.vote.infrastructure.client;

import com.pieceofcake.auction_service.vote.infrastructure.client.dto.in.MemberPieceRequestDto;
import com.pieceofcake.auction_service.vote.infrastructure.client.dto.out.MemberPieceResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "piece-service",
        url = "${piece-service.url}")
public interface PieceFeignClient {

    @PostMapping("/api/v1/piece/member/quantity")
    List<MemberPieceResponseDto> getMemberPieceQuantities(@RequestBody MemberPieceRequestDto memberPieceRequestDto);
}
