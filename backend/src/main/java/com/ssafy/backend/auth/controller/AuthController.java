package com.ssafy.backend.auth.controller;

import com.ssafy.backend.auth.model.*;
import com.ssafy.backend.auth.service.AuthService;
import com.ssafy.backend.common.ApiResponse;
import com.ssafy.backend.user.model.AnimeSelectionDto;
import com.ssafy.backend.user.model.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Authentication", description = "회원가입 · 이메일 인증 · 로그인 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 인증 코드 발송
     */
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, String>> sendCode(
            @Valid @RequestBody EmailSendRequestDto request
    ) {
        authService.sendEmailVerificationCode(request);
        return ResponseEntity.ok(Map.of("message", "인증코드 전송 완료"));
    }

    /**
     * 인증 코드 검증
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Boolean>> verifyEmail(
            @Valid @RequestBody EmailVerifyRequestDto request
    ) {
        authService.verifyEmailCode(request);
        return ResponseEntity.ok(Map.of("verified", true));
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(
            @Valid @RequestBody SignUpRequestDto request
    ) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // JWT 기반 로그아웃: 클라이언트가 토큰 삭제하면 로그아웃됨
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}

