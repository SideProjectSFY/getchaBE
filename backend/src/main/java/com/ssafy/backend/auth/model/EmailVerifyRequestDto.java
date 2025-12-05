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
public class EmailVerifyRequestDto {

    @Schema(description = "인증 중인 이메일")
    @Email
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "이메일로 받은 인증번호")
    @NotBlank(message = "인증번호는 필수입니다.")
    private String code;
}

