package com.ssafy.backend.payment.controller;

import com.ssafy.backend.payment.model.PaymentRequestDto;
import com.ssafy.backend.payment.model.PaymentResponseDto;
import com.ssafy.backend.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment", description = "골드 결제 (PortOne)")
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 결제 금액 검증 + merchantUid 발급
    @Operation(summary = "결제 준비")
    @PostMapping("/wallet/prepare")
    public ResponseEntity<PaymentResponseDto.Prepare> prepare(
            @AuthenticationPrincipal Long userId,
            @RequestBody PaymentRequestDto.Prepare request
    ){
        PaymentResponseDto.Prepare response = paymentService.prepareWalletCharge(userId, request);
        return ResponseEntity.ok(response);
    }

    // impUid(portOne 결제 고유 키) 사용해 실제 결제 정보와 일치하는 지 확인
    // 검증 성공 시 결제 상태 PAID, 지갑에 골드 충전
    @Operation(summary = "결제 완료 검증")
    @PostMapping("/wallet/complete")
    public ResponseEntity<PaymentResponseDto.Complete> complete(
            @AuthenticationPrincipal Long userId,
            @RequestBody PaymentRequestDto.Complete request
    ){
        PaymentResponseDto.Complete response = paymentService.completeWalletCharge(userId, request);
        return ResponseEntity.ok(response);
    }

    // portOne 서버 -> 백엔드 서버
    // 프론트에서 요청 누락 또는 네트워크 오류 발생한 경우 결제 상태 일치시키기 위해서 !
    @Operation(summary = "PortOne 웹훅 수신")
    @PostMapping("/webhook/portone")
    public ResponseEntity<Void> webhook(@RequestBody PaymentRequestDto.Webhook webhook){
        paymentService.handlePortOneWebhook(webhook);
        return ResponseEntity.ok().build();
    }
}
