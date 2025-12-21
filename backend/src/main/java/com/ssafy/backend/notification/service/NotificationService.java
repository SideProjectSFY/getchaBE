package com.ssafy.backend.notification.service;

import com.ssafy.backend.common.enums.NotificationType;
import com.ssafy.backend.notification.model.NotificationCursorResponseDto;
import com.ssafy.backend.notification.model.NotificationResponseDto;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    //1. 읽지 않은 알림 목록 조회
    List<NotificationResponseDto> getUnreadNotifications(Long userId);

    //2. 알림 생성
    NotificationResponseDto createNotification(Long userId, NotificationType type, Map<String, String> variables, Long goodsId);

    //3. 알림 단 건 읽음 처리
    void markAsRead(Long notificationId);

    //4. 알림 전체 읽음 처리
    void markAllAsRead(Long userId);

    //5. 읽지 않은 알림 + 페이징 처리
    NotificationCursorResponseDto getUnreadNotificationList(Long userId, Long cursorId);
}
