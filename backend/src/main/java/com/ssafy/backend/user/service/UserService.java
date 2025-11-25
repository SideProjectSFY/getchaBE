package com.ssafy.backend.user.service;

import com.ssafy.backend.user.model.UserRequestDto;
import com.ssafy.backend.user.model.UserResponseDto;

public interface UserService {
    UserResponseDto signUp(UserRequestDto user);
}
