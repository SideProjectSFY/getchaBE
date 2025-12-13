package com.ssafy.backend.anime.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnimeMapper {

    // 1. 검색어 입력해서 애니 조회
    List<TmdbAnimeEntityDto> findAnimeByKeyword(@Param("keyword") String keyword);

    // 2. DB 내 애니 저장
    void insertAnime(TmdbAnimeEntityDto anime);

    // 3. 애니 - 장르 매핑 저장
    void insertAnimeGenre(@Param("animeId") Long animeId,
                          @Param("genreId") Integer genreId);
}
