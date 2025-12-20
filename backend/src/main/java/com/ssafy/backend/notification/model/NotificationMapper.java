package com.ssafy.backend.notification.model;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    // 알림 감지 !!
    // 최초 입찰 발생한 경매 조회
    List<Map<String, Object>> findStartedAuctions();

    //종료된 경매 조회
    List<Map<String, Object>> findClosedAuctions();

    // 5분 안에 종료 될 경매 조회
    List<Map<String, Object>> findEndingInFiveMinutes();

    // 경매의 입찰자 목록
    List<Long> findBiddersByGoodsId(@Param("goodsId") Long goodsId);

    // 안읽은 알림 페이징 처리
    List<Notification> findUnreadByCursor(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            @Param("fetchSize") int fetchSize
    );
}
