package com.ssafy.backend.auth.model;

import com.ssafy.backend.user.model.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    private final String accessToken; //JWT Acess Token
    private final String tokenType; //Bearer
    private final Instant expiresAt; //토큰 만료 시각
    private final UserResponseDto user; //로그인한 사용자의 상세 정보
}

