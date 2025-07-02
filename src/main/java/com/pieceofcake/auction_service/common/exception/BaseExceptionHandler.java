package com.pieceofcake.auction_service.common.exception;

import com.pieceofcake.auction_service.common.entity.BaseResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    /**
     * 발생한 예외 처리
     */

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<BaseResponseEntity<Void>> BaseError(BaseException e) {
        BaseResponseEntity<Void> response = new BaseResponseEntity<>(e.getStatus());
        log.error("BaseException -> {}({})", e.getStatus(), e.getStatus().getMessage(), e);
        return new ResponseEntity<>(response, response.httpStatus());
    }

    @ExceptionHandler(FeignClientException.class)
    protected ResponseEntity<BaseResponseEntity<Void>> handleFeignClientException(FeignClientException e) {
        // 원격 서비스의 응답 정보를 그대로 클라이언트에게 반환
        BaseResponseEntity<Void> response = new BaseResponseEntity<>(
                org.springframework.http.HttpStatus.valueOf(e.getStatusCode()),
                e.isSuccess(),
                e.getErrorMessage(),
                e.getErrorCode(),
                null
        );

        log.error("FeignClientException -> code: {}, message: {}", e.getErrorCode(), e.getErrorMessage(), e);
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<BaseResponseEntity<Void>> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "입력값 검증에 실패했습니다";
        
        BaseResponseEntity<Void> response = new BaseResponseEntity<>(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                false,
                errorMessage,
                4000,
                null
        );
        
        log.error("ValidationException -> field: {}, message: {}", 
                fieldError != null ? fieldError.getField() : "unknown", errorMessage, e);
        return ResponseEntity.badRequest().body(response);
    }
}
