package com.ssafy.backend.user.model;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    //회원가입
    public void insertUser(User user);
    public User findByEmail(@Param("email") String email);
    //관심 애니메이션 3개 저장
    public void insertLikedAnime(
            @Param("userId") Long userId,
            @Param("animeId1") Long animeId1,
            @Param("animeId2") Long animeId2,
            @Param("animeId3") Long animeId3
    );
    //관심 애니메이션 목록 조회
    public List<AnimeSelectionDto> findUserLikedAnimes(
            @Param("userId") Long userId
    );
    //프로필 이미지 저장 (liked_anime_id1)
    public String findProfileImage(
            @Param("userId") Long userId
    );
    //프로필 조회
    public User findById(
            @Param("userId") Long userId
    );
    //프로필 수정
    public void updateUser(User user);
    //회원 탈퇴
    public void deleteUser(@Param("userId") Long userId);
}
