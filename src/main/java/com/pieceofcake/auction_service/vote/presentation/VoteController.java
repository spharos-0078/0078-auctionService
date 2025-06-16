package com.pieceofcake.auction_service.vote.presentation;

import com.pieceofcake.auction_service.common.entity.BaseResponseEntity;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.vote.application.VoteService;
import com.pieceofcake.auction_service.vote.dto.in.CreateVoteRequestDto;
import com.pieceofcake.auction_service.vote.vo.in.CreateVoteRequestVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("")
    public BaseResponseEntity<Void> createVote(
            @RequestBody CreateVoteRequestVo createVoteRequestVo
            ) {
        voteService.createVote(CreateVoteRequestDto.from(createVoteRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }


}
