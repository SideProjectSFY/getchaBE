package com.ssafy.backend.user.controller;

import com.ssafy.backend.common.ApiResponse;
import com.ssafy.backend.user.model.UserRequestDto;
import com.ssafy.backend.user.model.UserResponseDto;
import com.ssafy.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 프로필 조회
     */
    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회")
    public ResponseEntity<UserResponseDto> getMyProfile(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

    /**
     * 내 프로필 수정
     */
    @PutMapping("/me")
    @Operation(summary = "내 프로필 수정")
    public ResponseEntity<UserResponseDto> updateMyProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserRequestDto dto
    ) {
        return ResponseEntity.ok(userService.updateMyProfile(userId, dto));
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<String> deleteMyAccount(
            @AuthenticationPrincipal Long userId
    ) {
        userService.deleteMyAccount(userId);
        return ResponseEntity.ok("회원 탈퇴 처리 완료");
    }
}
