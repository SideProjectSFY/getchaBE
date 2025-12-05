package com.ssafy.backend.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    @Schema(description = "로그인 이메일")
    @Email
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "로그인 비밀번호")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}

