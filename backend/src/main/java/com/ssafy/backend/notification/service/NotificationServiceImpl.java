package com.ssafy.backend.notification.service;

import com.ssafy.backend.common.enums.NotificationType;
import com.ssafy.backend.notification.model.Notification;
import com.ssafy.backend.notification.model.NotificationCursorResponseDto;
import com.ssafy.backend.notification.model.NotificationMapper;
import com.ssafy.backend.notification.model.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    // 알림 페이징 처리는 5개씩
    private static final int NOTIFICATION_PAGE_SIZE = 5;

    private final NotificationMapper notificationMapper;
    private final LongPollingManager longPollingManager;

    //1. 읽지 않은 알림 목록 조회
    @Override
    public List<NotificationResponseDto> getUnreadNotifications(Long userId) {
        List<Notification> list = notificationMapper.findUnreadByUserId(userId);
        return list.stream()
                .map(NotificationResponseDto::from)
                .toList();
    }

    //2. 알림 생성
    @Override
    public NotificationResponseDto createNotification(
            Long userId,
            NotificationType type,
            Map<String, String> variables,
            Long goodsId
    ) {
        // message 문장 만들기
        // ex : type.getMessage() = "축하드립니다! {itemName} 경매에 낙찰되었습니다!"
        // ex : variables = { "itemName" : "에렌 피규어" }
        String completeMessage = applyTemplate(type.getMessage(), variables);
        String link = buildLink(type, goodsId);

        // DB 저장
        Notification noti = Notification.builder()
                .userId(userId)
                .type(type)
                .message(completeMessage)
                .link(link)
                .createdAt(LocalDateTime.now())
                .build();
        notificationMapper.insertNotification(noti);

        // Long Polling 대기 중 유저 깨우기
        NotificationResponseDto dto = NotificationResponseDto.from(noti);
        longPollingManager.notifyUser(userId, List.of(dto));

        return NotificationResponseDto.from(noti);
    }

    //3. 알림 단 건 읽음 처리
    @Override
    public void markAsRead(Long notificationId) {
        notificationMapper.markAsRead(notificationId);
    }

    //4. 알림 전체 읽음 처리
    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    //메세지 템플릿 변수 치환 ex : {itemName}
    private String applyTemplate(String template, Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) {
            return template;
        }
        String msg = template;
        // 한문장에 itemName, userName 두 개 모두 있을 수 있으니까 ..
        for(String key : variables.keySet()) {
            msg = msg.replace("{" + key + "}", variables.get(key));
        }
        return msg;
    }

    private String buildLink(NotificationType type, Long goodsId) {
        if (goodsId == null || type.getLinkTemplate() == null) {
            return null;
        }
        return type.getLinkTemplate().replace("{goodsId}", goodsId.toString());
    }


    /* ================================
     *  SCHEDULER
     * ================================
     *
     * 1분마다 아래 3가지 자동 감지:
     * - AUCTION_STARTED (경매 시작)
     * - AUCTION_ENDING_SOON (종료 5분 전)
     * - AUCTION_CLOSED (경매 종료)
     *
     */

    @Scheduled(fixedRate = 60_000L) // 1분마다
    public void autoActionNotification() {
        handleStarted();
        handleEndingSoon();
        handleClosed();
    }

    //공통 알림 처리
    private void processRows(List<Map<String, Object>> rows, NotificationType type){
        for(Map<String, Object> row : rows){
            Long goodsId = ((Number) row.get("goodsId")).longValue();
            Long sellerId = ((Number) row.get("sellerId")).longValue();
            String itemName = (String) row.get("itemName");

            // 판매자 알림
            createNotification(
                    sellerId,
                    type,
                    Map.of("itemName", itemName),
                    goodsId
            );

            // 경매 시작이면 판매자한테만 알림 가도록
            if(type == NotificationType.AUCTION_STARTED) continue;

            // 구매자 알림
            List<Long> bidderIds = notificationMapper.findBiddersByGoodsId(goodsId);
            for (Long bidderId : bidderIds) {
                createNotification(
                        bidderId,
                        type,
                        Map.of("itemName", itemName),
                        goodsId
                );
            }
        }
    }

    // 경매 시작
    private void handleStarted(){
        processRows(
                notificationMapper.findStartedAuctions(),
                NotificationType.AUCTION_STARTED
        );
    }

    // 경매 종료
    private void handleClosed(){
        processRows(
                notificationMapper.findClosedAuctions(),
                NotificationType.AUCTION_CLOSED
        );
    }

    // 경매 종료 5분전
    private void handleEndingSoon(){
        processRows(
                notificationMapper.findEndingInFiveMinutes(),
                NotificationType.AUCTION_ENDING_SOON
        );
    }

    //5. 읽지 않은 알림 + 페이징 처리
    @Override
    public NotificationCursorResponseDto getUnreadNotificationList(Long userId, Long cursorId){

        List<Notification> fetched = notificationMapper.findUnreadByCursor(userId, cursorId, NOTIFICATION_PAGE_SIZE + 1);

        boolean hasMore = fetched.size() > NOTIFICATION_PAGE_SIZE;

        // 알림 5개까지만 (혹은 미만)
        List<Notification> page = hasMore ? fetched.subList(0, NOTIFICATION_PAGE_SIZE) : fetched;

        List<NotificationResponseDto> items = new ArrayList<>();

        for (Notification notification : page){
            items.add(NotificationResponseDto.from(notification));
        }

        // 다음의 시작점
        Long nextCursorId = page.isEmpty() ? null : page.get(page.size() - 1).getId();

        return NotificationCursorResponseDto.builder()
                .items(items)
                .nextCursorId(nextCursorId)
                .hasMore(hasMore)
                .build();
    }
}
