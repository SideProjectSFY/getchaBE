package com.ssafy.backend.notification.model;

import com.ssafy.backend.common.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto {
    //프론트엔드로 내려가는 DTO
    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private String link;
    private String createdAt;
    private String readAt;

    // NotificationResponse 는 단순 데이터 전달이 아닌 매번 가공이 필요해서 생성자 어노테이션 안씀 !
    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .message(notification.getMessage())
                .link(notification.getLink())
                .createdAt(notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : null)
                .readAt(notification.getReadAt() != null ? notification.getReadAt().toString() : null)
                .build();
    }
}
