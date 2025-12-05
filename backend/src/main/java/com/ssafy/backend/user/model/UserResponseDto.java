package com.ssafy.backend.user.model;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String nickname;
    private String name;
    private String email;
    private List<AnimeSelectionDto> likedAnimes;
    private String accountNum;
    private String accountBank;
}
