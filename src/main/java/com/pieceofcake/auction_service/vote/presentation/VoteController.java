package com.pieceofcake.auction_service.vote.presentation;

import com.pieceofcake.auction_service.common.entity.BaseResponseEntity;
import com.pieceofcake.auction_service.common.entity.BaseResponseStatus;
import com.pieceofcake.auction_service.vote.application.VoteDetailService;
import com.pieceofcake.auction_service.vote.application.VoteService;
import com.pieceofcake.auction_service.vote.dto.in.CreateVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.CreateVoteRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteDetailRequestDto;
import com.pieceofcake.auction_service.vote.dto.in.ReadVoteRequestdto;
import com.pieceofcake.auction_service.vote.dto.out.ReadVoteListResponseDto;
import com.pieceofcake.auction_service.vote.vo.in.CreateVoteDetailRequestVo;
import com.pieceofcake.auction_service.vote.vo.in.CreateVoteRequestVo;
import com.pieceofcake.auction_service.vote.vo.in.ReadVoteDetailRequestVo;
import com.pieceofcake.auction_service.vote.vo.in.ReadVoteRequestVo;
import com.pieceofcake.auction_service.vote.vo.out.ReadVoteDetailResponseVo;
import com.pieceofcake.auction_service.vote.vo.out.ReadVoteListResponseVo;
import com.pieceofcake.auction_service.vote.vo.out.ReadVoteResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final VoteDetailService voteDetailService;

    @Operation(
            summary = "투표 생성 API",
            description = "투표를 생성하는 API입니다.\n\n" +
                    "- 요청 본문에 `CreateVoteRequestVo` 객체를 포함해야 합니다.\n\n" +
                    "- 어느 상황에 투표가 생성되는지는 잘 모르겠습니다.\n\n" +
                    "  - 우선 jwt에 있는 memberUuid를 startingMemberUuid로 삼을까 합니다.\n\n" +
                    "- createVoteRequestVo 에는 {productUuid, startingPrice, startDate, endDate}가 포함되어야 합니다.\n\n" +
                    "  - `startingPrice`는 '상품이 이 가격에 경매 시작이면 경매 할건가?'라는 질문때문에 넣었습니다.\n\n" +
                    "  - 사실 빼도 되나? 싶긴 한데, 현재 DB에는 nullable=false 로 되어 있습니다. 수정하고싶으면 회의 시작..."
    )
    @PostMapping("")
    public BaseResponseEntity<Void> createVote(
            @RequestBody CreateVoteRequestVo createVoteRequestVo,
            @RequestHeader(value = "X-Member-Uuid") String memberUuid
            ) {
        voteService.createVote(CreateVoteRequestDto.from(createVoteRequestVo, memberUuid));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "투표 조회 API",
            description = "특정 상품에 대한 투표를 조회하는 API입니다.\n\n" +
                    "- 요청 파라미터로 `productUuid`를 포함해야 합니다.\n" +
                    "- 해당 상품에 대한 투표 정보가 반환됩니다.\n" +
                    "  - response의 {agreeCount, disagreeCount, noVoteCount, totalCount} 는 투표 진행중엔 null, 투표 종료 후 생성됩니다.\n" +
                    "  - response의 status는 [ READY, OPEN, CLOSED_ACCEPTED, CLOSED_REJECTED, DELETED ]\n" +
                    "    - 이 때 투표 결과 찬성이면 'CLOSED_ACCEPTED', 반대면 'CLOSED_REJECTED' 입니다."
    )
    @GetMapping("")
    public BaseResponseEntity<ReadVoteResponseVo> readVote(
            @RequestParam String productUuid
    ) {
        ReadVoteRequestVo readVoteRequestVo = ReadVoteRequestVo.builder()
                .productUuid(productUuid)
                .build();

        ReadVoteResponseVo result = voteService.readVote(
                ReadVoteRequestdto.from(readVoteRequestVo)
        ).toVo();

        return new BaseResponseEntity<>(result);
    }

    @Operation(
            summary = "투표 상세 생성 API",
            description = "사용자가 한 투표를 생성하는 API입니다.\n\n" +
                    "- 요청 본문에 `CreateVoteDetailRequestVo` 객체를 포함해야 합니다.\n\n" +
                    "- `CreateVoteDetailRequestVo`에는 {voteUuid, voteChoice}가 포함되어야 합니다.\n\n" +
                    "  - voteChoice [ AGREE, DISAGREE, ABSTAIN ] 이 있습니다. 기권은 그냥 넣었습니다." +
                    "- 투표 상세는 회원이 투표에 참여할 때 생성됩니다.\n\n" +
                    "- 이미 투표한 사용자의 경우, 투표 create가 아니라 update가 됩니다."
    )
    @PostMapping("/detail")
    public BaseResponseEntity<Void> createVoteDetail(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody CreateVoteDetailRequestVo createVoteDetailRequestVo
            ) {
        voteDetailService.createVoteDetail(CreateVoteDetailRequestDto.from(createVoteDetailRequestVo, memberUuid));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "투표 상세 조회 API",
            description = "사용자가 한 투표를 조회하는 API입니다.\n\n" +
                    "- path variable 로 `voteUuid`를 포함해야 합니다.\n\n" +
                    "- JWT에서 memberUuid 를 파싱합니다. (내 투표만 조회할 수 있음)\n\n" +
                    "- response 는 { voteUuid, voteChoice }.\n\n" +
                    "- 응답에는 투표 참여자의 선택(찬성, 반대, 기권)과 투표 상태 등이 포함됩니다."
    )
    @GetMapping("/detail")
    public BaseResponseEntity<ReadVoteDetailResponseVo> getVoteDetail(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestParam String voteUuid
            ) {
        ReadVoteDetailRequestVo readVoteDetailRequestVo = ReadVoteDetailRequestVo.builder()
                .voteUuid(voteUuid)
                .memberUuid(memberUuid)
                .build();

        ReadVoteDetailResponseVo result = voteDetailService.readVoteDetail(
                ReadVoteDetailRequestDto.from(readVoteDetailRequestVo)
        ).toVo();

        return new BaseResponseEntity<>(result);
    }

    @Operation(
            summary = "투표 목록 조회 API",
            description = "모든 투표를 조회하거나 특정 상태의 투표만 조회하는 API입니다.\n\n" +
                    "- 선택적 요청 파라미터로 `status`를 포함할 수 있습니다.\n" +
                    "- status 파라미터가 있으면 해당 상태의 투표만 반환합니다.\n" +
                    "- status 파라미터가 없으면 모든 투표를 반환합니다.\n" +
                    "- 가능한 status 값: [READY, OPEN, CLOSED_ACCEPTED, CLOSED_REJECTED, DELETED]"
    )
    @GetMapping("/list")
    public BaseResponseEntity<List<ReadVoteListResponseVo>> getVoteList(
            @RequestParam(required = false) String status
    ) {
        return new BaseResponseEntity<>(voteService.readVoteList(status)
                .stream()
                .map(ReadVoteListResponseDto::toVo)
                .toList());
    }

}
