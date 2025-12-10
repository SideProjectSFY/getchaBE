package com.ssafy.backend.notification.model;

import com.ssafy.backend.common.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private String link;
}