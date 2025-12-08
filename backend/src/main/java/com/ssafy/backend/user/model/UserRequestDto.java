package com.ssafy.backend.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {

    // 마이페이지 수정

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "계좌번호는 필수입니다.")
    private String accountNum;

    @NotBlank(message = "은행명은 필수입니다.")
    private String accountBank;

    private Long likedAnimeId1;
    private Long likedAnimeId2;
    private Long likedAnimeId3;
}
