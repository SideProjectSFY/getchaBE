package com.ssafy.backend.user.controller;

import com.ssafy.backend.goods.model.GoodsResponseDto;
import com.ssafy.backend.user.model.UserRequestDto;
import com.ssafy.backend.user.model.UserResponseDto;
import com.ssafy.backend.user.service.UserService;
import com.ssafy.backend.wish.model.WishResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "USER API", description = "사용자 프로필 조회, 수정, 회원탈퇴 API")
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

    /**
     * 마이페이지 내 사용자가 등록한 굿즈 목록 조회
     */
    @Operation(
            summary = "사용자가 등록한 굿즈 목록 조회",
            description = "로그인한 사용자가 등록한 굿즈(=경매) 목록을 조회합니다."
    )
    @GetMapping("/me/goods/registered")
    public ResponseEntity<List<GoodsResponseDto.MyPageInRegisteredGoodsCard>> getAllRegisteredGoods(
            @AuthenticationPrincipal Long loginUserId) {
        List<GoodsResponseDto.MyPageInRegisteredGoodsCard> allRegisteredGoods = userService.getAllRegisteredGoods(loginUserId);
        return ResponseEntity.ok(allRegisteredGoods);
    }

    /**
     * 마이페이지 내 사용자가 참여한 굿즈 목록 조회
     */
    @Operation(
            summary = "사용자가 참여한 굿즈 목록 조회",
            description = "로그인한 사용자가 참여한 굿즈(=경매) 목록을 조회합니다."
    )
    @GetMapping("/me/goods/participated")
    public ResponseEntity<List<GoodsResponseDto.MyPageInParticipatedGoodsCard>> getAllParticipatedGoods(
            @AuthenticationPrincipal Long loginUserId) {
        List<GoodsResponseDto.MyPageInParticipatedGoodsCard> allRegisteredGoods = userService.getAllParticipatedGoods(loginUserId);
        return ResponseEntity.ok(allRegisteredGoods);
    }

    /**
     * 마이페이지 내 사용자가 찜한 굿즈 목록 조회
     */
    @Operation(
            summary = "사용자가 찜한 굿즈 목록 조회",
            description = "마이페이지 내 사용자가 찜한 굿즈 목록 조회합니다.")
    @GetMapping("/me/wish")
    public ResponseEntity<List<WishResponseDto.WishedGoodsAll>> getAllWishedGoods(
            @AuthenticationPrincipal Long loginUserId) {
        List<WishResponseDto.WishedGoodsAll> allWishedGoods = userService.getAllWishedGoods(loginUserId);
        return ResponseEntity.ok(allWishedGoods);
    }
}
