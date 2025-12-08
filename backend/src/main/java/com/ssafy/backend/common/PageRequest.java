package com.ssafy.backend.common;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * PageRequest : 페이지네이션 요청 정보를 표현하는 공통 DTO 클래스
 *
 */

@Getter
@Setter
public class PageRequest {

    @Min(value = 1, message = "page는 1 이상이어야 합니다.")
    private int page = 1;

    @Min(value = 1, message = "size는 1 이상이어야 합니다.")
    private int size = 10;

    private String sort = "createdAt"; // 기본값
    private String searchName;         // 검색명

    // Mybatis 에서 #{offset} 을 만나면 호출해서 값을 가져온다.
    public int getOffset() {
        return (page - 1) * size;
    }
}
