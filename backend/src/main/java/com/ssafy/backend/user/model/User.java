package com.ssafy.backend.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private Long liked_anime_id1;
    private Long liked_anime_id2;
    private Long liked_anime_id3;
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
