package com.ssafy.backend.common;

import lombok.Getter;
import lombok.Setter;

/**
 * PageRequest : 페이지네이션 요청 정보를 표현하는 공통 DTO 클래스
 *
 */

@Getter
@Setter
public class PageRequest {

    private int page = 1;
    private int size = 10;
    private int pageOffset;
    private String sort = "createdAt"; // 기본값
    private String searchName;         // 검색명

    public int getOffset() {
        return (page - 1) * size;
    }
}
