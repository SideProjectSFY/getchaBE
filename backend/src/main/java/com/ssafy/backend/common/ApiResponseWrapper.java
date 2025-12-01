package com.ssafy.backend.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();

        // Swagger 문서는 묶지 않기
        if (uri.contains("swagger")
                || uri.contains("api-docs")
                || uri.contains("swagger-ui")
                || uri.contains("v3/api-docs")) {
            return false;
        }

        return true; // 모든 컨트롤러에 적용
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType contentType,
                                  Class converterType,
                                  ServerHttpRequest req,
                                  ServerHttpResponse res) {

        // 이미 ApiResponse인 경우 -> 그대로 return
        if (body instanceof ApiResponse) {
            return body;
        }

        // ResponseEntity 인 경우
        if (body instanceof ResponseEntity<?> responseEntity) {
            Object originalBody = responseEntity.getBody();

            // body 가 null인 경우
            if (originalBody == null) {
                return responseEntity;
            }

            // 새 ApiResponse 로 감싼 후 다시 ResponseEntity 로 반환
            ApiResponse<?> wrapped = ApiResponse.ok(originalBody);

            return ResponseEntity
                    .status(responseEntity.getStatusCode())
                    .headers(responseEntity.getHeaders())
                    .body(wrapped);
        }

        // String 타입은 수동 변환 필요
        if (body instanceof String) {
            try {
                return new ObjectMapper().writeValueAsString(ApiResponse.ok(body));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 그 외 일반 객체는 ApiResponse로 감싸기
        return ApiResponse.ok(body);
    }
}
