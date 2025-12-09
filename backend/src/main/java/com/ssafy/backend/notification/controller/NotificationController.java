package com.ssafy.backend.notification.controller;

import com.ssafy.backend.auth.service.jwt.JwtTokenProvider;
import com.ssafy.backend.notification.model.NotificationRequestDto;
import com.ssafy.backend.notification.model.NotificationResponseDto;
import com.ssafy.backend.notification.service.LongPollingManager;
import com.ssafy.backend.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@Tag(name = "Notification", description = "알림")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LongPollingManager longPollingManager;

    //Request에서 Authorization 헤더 추출
    private Long extraUserId(HttpServletRequest request){
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserId(token);
    }

    // 1. 읽지 않은 알림 목록 조회
    @GetMapping("")
    @Operation(summary = "읽지 않은 알림 목록 조회")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNoti(
            HttpServletRequest request
    ) {
        Long userId = extraUserId(request);
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    // 2. 알림 생성
    @PostMapping("")
    @Operation(summary = "알림 생성 (테스트)")
    public ResponseEntity<NotificationResponseDto> createNotification(
            @RequestBody NotificationRequestDto dto,
            HttpServletRequest request
    ) {
        Long userId = extraUserId(request);
        return ResponseEntity.ok(notificationService.createNotification(userId, dto.getType(), dto.getVars(), dto.getGoodsId()));
    }

    // 3. 알림 단 건 읽음 처리
    @PatchMapping("/{notificationId}")
    @Operation(summary = "알림 단 건 읽음 처리")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // 4. 알림 전체 읽음 처리
    @PatchMapping("/read-all")
    @Operation(summary = "알림 전체 읽음 처리")
    public ResponseEntity<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = extraUserId(request);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // 5. Long Polling
    @Operation(
            summary = "Long Polling 실시간 알림 스트림",
            description =
                    """
                    1. 서버 - 읽지 않은 알림 확인
                    2. 읽지 않은 알림이 있을 경우 즉시 반환
                    3. 없다면 최대 60초 동안 대기
                    4. 새 알림이 도착 시 즉시 응답
                    5. 60초 동안 아무 이벤트도 없다면 204 No Content 반환
                    """
    )
    @GetMapping("/stream")
    public DeferredResult<ResponseEntity<List<NotificationResponseDto>>> stream(
            HttpServletRequest request
    ) {
        Long userId = extraUserId(request);

        // unread 알림 있는 지 체크 !
        List<NotificationResponseDto> unread = notificationService.getUnreadNotifications(userId);

        // 있으면 클라이언트로 즉시 응답
        if (!unread.isEmpty()) {
            DeferredResult<ResponseEntity<List<NotificationResponseDto>>> result = new DeferredResult<>();
            result.setResult(ResponseEntity.ok(unread));
            return result;
        }

        // 없으면 Long Polling 대기
        DeferredResult<ResponseEntity<List<NotificationResponseDto>>> result =
                new DeferredResult<>(60_000L, ResponseEntity.noContent().build());

        longPollingManager.addWaiter(userId, result);
        return result;
    }
}
