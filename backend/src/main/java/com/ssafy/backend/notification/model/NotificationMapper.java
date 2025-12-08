package com.ssafy.backend.notification.model;

import com.ssafy.backend.common.enums.NotificationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    //1. 읽지 않은 알림 목록 조회
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    //2. 알림 insert
    void insertNotification(Notification notification);

    //3. 알림 단 건 읽음 처리
    void markAsRead(@Param("notificationId") Long notificationId);

    //4. 알림 전체 읽음 처리
    void markAllAsRead(@Param("userId") Long userId);
}
