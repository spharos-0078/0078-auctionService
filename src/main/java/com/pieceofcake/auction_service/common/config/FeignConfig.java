package com.pieceofcake.auction_service.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.auction_service.common.exception.FeignClientException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

@Slf4j
@Configuration
public class FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                requestTemplate.header("Authorization", token);
            }
        }
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            String body = "";
            try {
                // 1) 바디 읽기
                body = Util.toString(response.body().asReader());

                // 2) ObjectMapper 설정: 알 수 없는 프로퍼티 무시
                ObjectMapper mapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                // 3) 파싱
                ErrorResponse err = mapper.readValue(body, ErrorResponse.class);

                // 4) FeignClientException 생성
                return new FeignClientException(
                        response.status(),
                        err.isSuccess(),
                        err.getCode(),
                        err.getMessage()
                );

            } catch (Exception ex) {
                String url = response.request().url();
                int status = response.status();
                body = "읽기 실패: " + ex.getMessage();
                log.error("Feign 요청 실패 - methodKey: {}, URL: {}, status: {}, body: {}", methodKey, url, status, body);

                // 파싱 실패나 IO 에러도 여전히 FeignClientException 으로 감싸기
                return new FeignClientException(
                        response.status(),
                        false,
                        0,       // 파싱 실패시 기본 코드
                        body     // raw body(혹은 ex.getMessage())
                );
            }
        }
    }
    @Data
    public static class ErrorResponse {
        private String httpStatus;
        private boolean isSuccess;
        private String message;
        private int code;
        private Object result;
    }
}