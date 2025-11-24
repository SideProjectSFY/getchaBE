package com.ssafy.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    FIGURE("피규어"),
    PHOTOCARD("포토카드"),
    ACRYLICSTAND("아크릴스탠드"),
    KEYRING("키링"),
    DOLL("인형"),
    POSTER("포스터"),
    BADGE("뱃지류"),
    OTHER("기타");

    private final String category;
}
