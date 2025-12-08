package com.ssafy.backend.auth.service;

import com.ssafy.backend.auth.model.*;
import com.ssafy.backend.user.model.AnimeSelectionDto;
import com.ssafy.backend.user.model.UserResponseDto;

import java.util.List;

public interface AuthService {

    /**
     * 인증코드 생성 & 발송
     */
    void sendEmailVerificationCode(EmailSendRequestDto request);

    /**
     * 인증코드 검증
     */
    void verifyEmailCode(EmailVerifyRequestDto request);

    /**
     * 회원가입
     */
    UserResponseDto signUp(SignUpRequestDto request);

    /**
     * 로그인
     */
    LoginResponseDto login(LoginRequestDto request);

}

