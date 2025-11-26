package com.ssafy.backend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * PageResponse<T> : 페이지네이션 응답 데이터를 담는 제네릭 DTO 클래스
 * @param <T>
 *
 */

@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> items;      // 조회된 데이터 목록
    private int currentPage;    // 현재 페이지 번호
    private int totalPages;     // 전체 페이지 수
    private long totalItems;    // 전체 데이터 개수

}
