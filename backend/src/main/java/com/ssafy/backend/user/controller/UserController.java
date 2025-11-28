package com.ssafy.backend.user.controller;

import com.ssafy.backend.common.ApiResponse;
import com.ssafy.backend.user.model.UserRequestDto;
import com.ssafy.backend.user.model.UserResponseDto;
import com.ssafy.backend.user.service.UserService;
import com.ssafy.backend.auth.service.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * JWT 에서 사용자 ID 반환
     */
    private Long extractUserId(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserId(token);
    }

    /**
     * 내 프로필 조회
     */
    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyProfile(
            HttpServletRequest request
    ) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(ApiResponse.ok(userService.getMyProfile(userId)));
    }

    /**
     * 내 프로필 수정
     */
    @PutMapping("/me")
    @Operation(summary = "내 프로필 수정")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateMyProfile(
            HttpServletRequest request,
            @RequestBody UserRequestDto dto
    ) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(ApiResponse.ok(userService.updateMyProfile(userId, dto)));
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<ApiResponse<String>> deleteMyAccount(
            HttpServletRequest request
    ) {
        Long userId = extractUserId(request);
        userService.deleteMyAccount(userId);
        return ResponseEntity.ok(ApiResponse.ok("회원 탈퇴 처리 완료"));
    }
}
