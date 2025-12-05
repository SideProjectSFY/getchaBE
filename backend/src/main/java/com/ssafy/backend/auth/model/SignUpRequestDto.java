package com.ssafy.backend.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {

    @Schema(description = "사용자 닉네임")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Schema(description = "실명")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "로그인에 사용할 이메일")
    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Schema(description = "관심 애니메이션 목록")
    @NotEmpty(message = "관심 애니메이션 3개를 선택해주세요.")
    @Size(min = 3, max = 3, message = "관심 애니메이션 3개를 선택해주세요.")
    private List<Long> likedAnimeIds;

    @Schema(description = "입금 계좌번호")
    @NotBlank(message = "계좌번호를 입력해주세요.")
    private String accountNum;

    @Schema(description = "은행명")
    @NotBlank(message = "계좌은행을 입력해주세요.")
    private String accountBank;
}

