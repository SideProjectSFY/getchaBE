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

    @Operation(summary = "결제 준비")
    @PostMapping("/wallet/prepare")
    public ResponseEntity<PaymentResponseDto.Prepare> prepare(
            @AuthenticationPrincipal Long userId,
            @RequestBody PaymentRequestDto.Prepare request
    ){
        PaymentResponseDto.Prepare response = paymentService.prepareWalletCharge(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 완료")
    @PostMapping("/wallet/complete")
    public ResponseEntity<PaymentResponseDto.Complete> complete(
            @AuthenticationPrincipal Long userId,
            @RequestBody PaymentRequestDto.Complete request
    ){
        PaymentResponseDto.Complete response = paymentService.completeWalletCharge(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "PortOne 웹훅 수신")
    @PostMapping("/webhook/portone")
    public ResponseEntity<Void> webhook(@RequestBody PaymentRequestDto.Webhook webhook){
        paymentService.handlePortOneWebhook(webhook);
        return ResponseEntity.ok().build();
    }
}
