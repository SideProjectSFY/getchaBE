package com.ssafy.backend.user.model;
import lombok.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    private String nickname;
    private String name;
    private String email;
    private String password;
    private List<Long> likedAnimeIds;
    private String accountNum;
    private String accountBank;
}
