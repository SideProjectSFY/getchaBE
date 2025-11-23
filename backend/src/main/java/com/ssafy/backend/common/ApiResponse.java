package com.ssafy.backend.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * ApiResponse<T> : 성공/실패 여부와 관계없이 동일한 응답 형태를 보장하기 위한 CustomDTO
 * ✔ 응답 구조 예시
 *  * {
 *  *   "status": 200,             # HTTP 상태코드
 *  *   "message": "SUCCESS",      # 상태 설명 또는 에러 메시지
 *  *   "data": { ... }            # API에서 실제 반환하려는 데이터(payload)
 *  * }
 * */

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .data(null)
                .build();
    }
}
