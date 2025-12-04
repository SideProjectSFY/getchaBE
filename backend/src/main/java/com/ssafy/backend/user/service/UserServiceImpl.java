package com.ssafy.backend.user.service;

import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.user.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    /**
     * 내 프로필 조회
     */
    @Override
    public UserResponseDto getMyProfile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null || user.getDeletedAt() != null) {
            throw new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        List<AnimeSelectionDto> likedAnimes = userMapper.findUserLikedAnimes(userId);

        return UserResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .accountNum(user.getAccountNum())
                .accountBank(user.getAccountBank())
                .likedAnimes(likedAnimes)
                .build();
    }

    /**
     * 내 프로필 수정
     */
    @Override
    @Transactional
    public UserResponseDto updateMyProfile(Long userId, UserRequestDto request) {

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        validateLikedAnimeIds(request);

        User updatePayload = User.builder()
                .id(userId)
                .nickname(request.getNickname())
                .accountNum(request.getAccountNum())
                .accountBank(request.getAccountBank())
                .likedAnimeId1(request.getLikedAnimeId1())
                .likedAnimeId2(request.getLikedAnimeId2())
                .likedAnimeId3(request.getLikedAnimeId3())
                .build();

        userMapper.updateUser(updatePayload);

        List<AnimeSelectionDto> likedAnimes = userMapper.findUserLikedAnimes(userId);

        return UserResponseDto.builder()
                .id(userId)
                .nickname(request.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .accountNum(request.getAccountNum())
                .accountBank(request.getAccountBank())
                .likedAnimes(likedAnimes)
                .build();
    }

    private void validateLikedAnimeIds(UserRequestDto request) {
        if (request.getLikedAnimeId1() == null
                || request.getLikedAnimeId2() == null
                || request.getLikedAnimeId3() == null) {
            throw new CustomException("관심 애니메이션 3개를 모두 선택해주세요.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 회원 탈퇴
     */
    @Override
    @Transactional
    public void deleteMyAccount(Long userId) {
        userMapper.deleteUser(userId);
    }
}
