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
    private int page;
    private int size;
    private String sort = "createdAt"; // 기본값
    private String searchName;         // 검색명

    public int getOffset() {
        return (page - 1) * size;
    }
}
