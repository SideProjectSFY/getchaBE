package com.ssafy.backend.auth.model;

import com.ssafy.backend.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthMapper {
    /**
     * 회원 정보를 user 에 저장
     */
    void insertUser(User user);

    /**
     * 이메일 기준으로 user 조회
     */
    User findActiveUserByEmail(@Param("email") String email);

    /**
     * 이메일 중복 여부 확인
     */
    boolean existsByEmail(@Param("email") String email);

    /**
     * 이메일로 user ID 체크
     */
    Long findIdByEmail(@Param("email") String email);

    /**
     * 관심 애니 개수 체크
     */
    int countAnimeByIds(@Param("animeIds") List<Long> animeIds);

}
