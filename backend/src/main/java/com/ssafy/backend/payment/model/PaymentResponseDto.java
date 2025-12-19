package com.ssafy.backend.payment.model;

import lombok.Builder;
import lombok.Getter;

public class PaymentResponseDto {
    //결제 준비 응답
    @Getter
    @Builder
    public static class Prepare {
        private String merchantUid;
        private Integer amount;
    }

    //결제 완료 응답
    @Getter
    @Builder
    public static class Complete {
        private String status;
        private String merchantUid;
        private Integer amount;
        private Integer walletBalance; //충전 후 잔액
        private String failReason; //실패 사유
    }
}
