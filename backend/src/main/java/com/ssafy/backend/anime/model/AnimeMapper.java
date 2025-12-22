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

    // 4. 전체 애니 임베딩 생성
    List<TmdbAnimeEntityDto> findAllForEmbedding();

    // 5. 유저가 좋아하는 애니 3개에 대해서만 임베딩 생성
    TmdbAnimeEntityDto findByIdForEmbedding(@Param("id") Long id);

    // 6. 애니 1개의 장르 문자열 조회
    List<String> findGenresById(@Param("animeId") Long animeId);

    // 7. 콘텐츠 유사도 기반 추천 결과 반환
    List<TmdbAnimeEntityDto> findByIds(@Param("ids") List<Long> ids);
}
