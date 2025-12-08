package com.ssafy.backend.user.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findById(@Param("userId") Long userId);

    /**
     * 프로필 이미지 저장.
     */
    String findProfileImage(@Param("userId") Long userId);

    /**
     * 관심 애니 리스트 조회
     */
    List<AnimeSelectionDto> findUserLikedAnimes(@Param("userId") Long userId);

    /**
     * 프로팔 수정
     */
    void updateUser(User user);

    /**
     * 회원 탈퇴
     */
    void deleteUser(@Param("userId") Long userId);
}
