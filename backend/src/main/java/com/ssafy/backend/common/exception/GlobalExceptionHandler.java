package com.ssafy.backend.common.exception;

import com.ssafy.backend.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 서비스 계층에서 throw new CustomException(...) 호출 -> 해당 메서드 자동 실행
     * */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {

        return ResponseEntity
                .status(e.getStatus())
                .body(ApiResponse.error(e.getStatus(), e.getMessage()));
    }

    /**
     * 잘못된 파라미터값이 들어올 경우 호출 -> 아래 메서드 자동 실행
     * */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException e) {

        log.warn("IllegalArgumentException e : " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    /**
     * 객체나 데이터를 찾으려할 때 요소가 없는 경우 호출 -> 아래 메서드 자동 실행
     * */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<?>> handleNoSuchElement(NoSuchElementException e) {

        log.warn("NoSuchElementException e : " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    /**
     * DTO @Valid 검증 실패 예외 처리
     * - RequestBody → DTO 변환 과정에서 필드 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        log.warn("MethodArgumentNotValidException e : " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "DTO @Valid 검증 실패하였습니다."));
    }



    /**
     * GET 쿼리 파라미터 바인딩 실패 예외 처리
     * - @ModelAttribute, QueryParam 등의 값 검증 실패
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<?>> handleBindException(BindException e) {

        log.warn("BindException e : " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "GET 쿼리 파라미터 바인딩에 실패하였습니다."));
    }



    /**
     * @Validated 파라미터 제약조건(Constraint) 위반 예외 처리
     * - RequestParam, PathVariable 값 검증 실패
     * - Enum 타입 잘못 전달된 경우 포함
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {

        log.warn("ConstraintViolationException e : " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "@Validated 파라미터 제약조건(Constraint) 을 위반하였습니다."));
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException e) {

        log.warn("AccessDeniedException e : " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(HttpStatus.FORBIDDEN, "수정 또는 삭제 권한이 없습니다."));
    }

    /**
     * 그 외 예외처리
     * */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {

        log.warn("Exception e : " + e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류"));
    }
}
