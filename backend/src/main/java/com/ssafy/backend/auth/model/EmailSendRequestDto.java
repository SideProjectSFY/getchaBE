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
public class EmailSendRequestDto {

    //이메일 인증번호를 보내기 위한 요청 객체

    @Schema(description = "인증 코드를 전송할 이메일")
    @Email
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
}

