package com.ssafy.backend.notification.service;

import com.ssafy.backend.common.enums.NotificationType;
import com.ssafy.backend.notification.model.Notification;
import com.ssafy.backend.notification.model.NotificationMapper;
import com.ssafy.backend.notification.model.NotificationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

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
}
