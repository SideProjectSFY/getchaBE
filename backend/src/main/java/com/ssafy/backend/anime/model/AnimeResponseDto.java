package com.ssafy.backend.anime.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnimeResponseDto {
    //프론트로 내려 줄 데이터
    private Long animeId;
    private String title;
    private String postUrl;
}
