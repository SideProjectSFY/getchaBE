package com.ssafy.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * CustomException : 개발자가 비즈니스 로직에서 의도적으로 던지는 예외
 * 처리 방식 : GlobalExceptionHandler가 받아서 공통 응답 형식으로 변환
 * ✔ 주의할 점 : CustomException은 "의도적으로 처리할 비즈니스 오류"에만 사용
 * ✔ 사용 예시 (Service 계층)
 *      if (!user.canBid()) {
 *          throw new CustomException("입찰 권한이 없습니다.", HttpStatus.FORBIDDEN);
 *      }
 * */

@Getter
public class CustomException extends RuntimeException {

  private final HttpStatus status;

  public CustomException(String message, HttpStatus status) {
      super(message);
      this.status = status;
  }
}
