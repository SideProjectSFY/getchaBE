package com.ssafy.backend.anime.model;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TmdbAnimeEntityDto {
    //DB 저장/조회
    private Long id;
    private String title;
    private String postUrl;
    private String overview;
    private Long voteAverage;
    private Long voteCount;
    private Long popularity;
}
