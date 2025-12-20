package com.ssafy.backend.notification.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NotificationCursorResponseDto {

    // 실제 내려줄 알림 목록
    private List<NotificationResponseDto> items;

    // 마지막 읽은 알림 id (다음 조회 시 기준)
    private Long nextCursorId;

    // 다음 페이지 여부
    private boolean hasMore;
}
