package com.ssafy.backend.user.service;

import com.ssafy.backend.user.model.UserResponseDto;
import com.ssafy.backend.user.model.UserRequestDto;

public interface UserService {

    /**
     * 내 프로필 조회
     */
    UserResponseDto getMyProfile(Long userId);

    /**
     * 내 프로필 수정
     */
    UserResponseDto updateMyProfile(Long userId, UserRequestDto request);

    /**
     * 회원 탈퇴
     */
    void deleteMyAccount(Long userId);
}
