package com.ssafy.backend.anime.model;

import lombok.Data;

@Data
public class AnimeRequestDto {
    //검색 키워드 요청
    private String keyword; // 관심있는 애니 입력창에 입력된 값
}
