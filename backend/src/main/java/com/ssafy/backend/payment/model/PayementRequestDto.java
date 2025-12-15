package com.ssafy.backend.payment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class PayementRequestDto {

    //결제 준비 요청
    //프론트 -> 서버 : amount 전달
    //서버 : merchantUid 생성 + payment(READY)
    @Getter
    @NoArgsConstructor
    public static class Prepare {
        private Integer amount;
    }

    //결제 완료 요청 (검증)
    @Getter
    @NoArgsConstructor
    public static class Complete {
        private String merchantId;
        private String impUid;
    }

    //포트원 웹훅 수신
    @Getter
    @NoArgsConstructor
    public static class Webhook {
        private String impUid;
        private String merchantUid;
        private String status;
    }
}
