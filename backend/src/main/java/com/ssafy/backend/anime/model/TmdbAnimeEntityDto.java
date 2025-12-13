package com.ssafy.backend.anime.model;

import lombok.*;

import java.util.List;

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

    // TMDB 장르 - 16(애니메이션) 외 다른 장르
    private List<Integer> genreIds;
}
