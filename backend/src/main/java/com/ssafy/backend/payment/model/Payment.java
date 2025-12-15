package com.ssafy.backend.payment.model;

import com.ssafy.backend.common.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long id;
    private Long userId;
    //주문번호
    private String merchantId;
    //포트원 결제 고유번호
    private String impUid;
    //결제 금액
    private Integer amount;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private String failReason;
}
