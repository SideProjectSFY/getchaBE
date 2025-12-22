package com.ssafy.backend.anime.service;

import com.ssafy.backend.anime.model.AnimeRequestDto;
import com.ssafy.backend.anime.model.AnimeResponseDto;
import java.util.List;

public interface AnimeService {

    // 애니 검색 및 db 저장
    // 1. db 검색
    // 2. db 존재 X && 검색어 2글자 이상 경우 TMDB 호출 및 db 저장
    // 3. db 결과 리스트 변환해서 응답
    List<AnimeResponseDto> searchAndSyncAnime(AnimeRequestDto animeRequestDto);

    // 초기 애니메이션 데이터 적재
    void bulkSyncAnimeFromTmdb(int pages);
}
