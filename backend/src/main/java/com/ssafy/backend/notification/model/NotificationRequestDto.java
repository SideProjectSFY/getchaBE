package com.ssafy.backend.notification.model;

import com.ssafy.backend.common.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class NotificationRequestDto {
    //경매 구현 전 테스트 용으로 사용 !
    @Schema(example = "AUCTION_WIN")
    private NotificationType type;

    @Schema(
            description = "알림 템플릿 변수",
            example = "{ \"itemName\": \"에렌 피규어\" }"
    )
    private Map<String, String> vars; // ex : {itemName}, {userName}
}
