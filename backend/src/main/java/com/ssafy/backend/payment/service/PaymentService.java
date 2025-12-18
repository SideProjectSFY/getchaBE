package com.ssafy.backend.payment.service;

import com.ssafy.backend.payment.model.PaymentRequestDto;
import com.ssafy.backend.payment.model.PaymentResponseDto;

public interface PaymentService {
    // 결제 준비 Ready
    PaymentResponseDto.Prepare prepareWalletCharge(Long loginUserId, PaymentRequestDto.Prepare request);

    // 결제 완료
    PaymentResponseDto.Complete completeWalletCharge(Long loginUserId, PaymentRequestDto.Complete request);

    // 웹훅 처리
    void handlePortOneWebhook(PaymentRequestDto.Webhook webhook);
}
