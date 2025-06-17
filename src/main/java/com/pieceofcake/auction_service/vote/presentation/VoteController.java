package com.pieceofcake.auction_service.vote.presentation;

import com.pieceofcake.auction_service.common.entity.BaseResponseEntity;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.vote.application.VoteDetailService;
import com.pieceofcake.auction_service.vote.application.VoteService;
import com.pieceofcake.auction_service.vote.dto.in.CreateVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.CreateVoteRequestDto;
import com.pieceofcake.auction_service.vote.vo.in.CreateVoteDetailRequestVo;
import com.pieceofcake.auction_service.vote.vo.in.CreateVoteRequestVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final VoteDetailService voteDetailService;

    @PostMapping("")
    public BaseResponseEntity<Void> createVote(
            @RequestBody CreateVoteRequestVo createVoteRequestVo
            ) {
        voteService.createVote(CreateVoteRequestDto.from(createVoteRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @PostMapping("/detail")
    public BaseResponseEntity<Void> createVoteDetail(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody CreateVoteDetailRequestVo createVoteDetailRequestVo
            ) {
        voteDetailService.createVoteDetail(CreateVoteDetailRequestDto.from(createVoteDetailRequestVo, memberUuid));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }


}
