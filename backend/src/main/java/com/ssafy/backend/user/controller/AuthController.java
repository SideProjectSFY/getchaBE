package com.ssafy.backend.user.controller;

import com.ssafy.backend.user.model.UserRequestDto;
import com.ssafy.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestDto request) {
        return ResponseEntity.ok(userService.signUp(request));
    }
    //이메일 인증코드 발송


    //이메일 인증 코드 검증


    //로그인

}