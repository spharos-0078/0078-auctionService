package com.pieceofcake.auction_service.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    /**
     * 200: 요청 성공
     **/
    SUCCESS(HttpStatus.OK, true, 200, "요청에 성공하였습니다."),


    /**
     * 400 : security 에러
     */
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, false, 400, "토큰이 존재하지 않습니다"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, false, 401, "유효하지 않은 접근 토큰입니다"),
    WRONG_JWT_TOKEN(HttpStatus.UNAUTHORIZED, false, 401, "다시 로그인 해주세요"),
    NO_SIGN_IN(HttpStatus.UNAUTHORIZED, false, 402, "로그인을 먼저 진행해주세요"),
    NO_ACCESS_AUTHORITY(HttpStatus.FORBIDDEN, false, 403, "접근 권한이 없습니다"),
    DISABLED_USER(HttpStatus.FORBIDDEN, false, 404, "비활성화된 계정입니다. 계정을 복구하시겠습니까?"),
    FAILED_TO_RESTORE(HttpStatus.INTERNAL_SERVER_ERROR, false, 405, "계정 복구에 실패했습니다. 관리자에게 문의해주세요."),

    /**
     * 3000: users service error
     */
    BID_NOT_FOUND(HttpStatus.NOT_FOUND, false, 3000, "입찰 내역을 찾을 수 없습니다."),

    // 3100: auction service error
    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, false, 3100, "경매를 찾을 수 없습니다."),
    AUCTION_NOT_ONGOING(HttpStatus.BAD_REQUEST, false, 3101, "진행 중인 경매가 아닙니다."),
    AUCTION_ALREADY_EXISTS(HttpStatus.CONFLICT, false, 3102, "이미 존재하는 경매상품입니다."),
    INVALID_AUCTION_STATUS(HttpStatus.BAD_REQUEST, false, 3103, "유효하지 않은 경매 상태입니다."),

    // 3200: vote service error
    ALREADY_VOTED(HttpStatus.BAD_REQUEST, false, 3200, "이미 투표한 경매입니다."),
    VOTE_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, false, 3201, "투표 내역을 찾을 수 없습니다."),
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, false, 3202, "투표를 찾을 수 없습니다."),
    MONEY_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, 3203, "금액 서비스와의 통신에 실패했습니다."),
    INVALID_VOTE_STATUS(HttpStatus.BAD_REQUEST, false, 3204, "유효하지 않은 투표 상태입니다."),



    INVALID_USER_INPUT(HttpStatus.BAD_REQUEST, false, 3000, "유효하지 않은 사용자 입력입니다.");

    private final HttpStatusCode httpStatusCode;
    private final boolean isSuccess;
    private final int code;
    private final String message;

}


