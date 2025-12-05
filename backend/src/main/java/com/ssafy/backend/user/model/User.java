package com.ssafy.backend.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private Long likedAnimeId1;
    private Long likedAnimeId2;
    private Long likedAnimeId3;
    private String name;
    private String nickname;
    private String email;
    private String password;
    private Boolean isAuth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String accountNum;
    private String accountBank;
}
