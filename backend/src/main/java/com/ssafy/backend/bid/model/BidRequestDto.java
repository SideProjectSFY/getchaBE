package com.ssafy.backend.bid.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BidRequestDto {

    @Schema(description = "입찰 등록 요청 DTO")
    @Getter
    @Builder
    public static class BidRegister {
        @Schema(hidden = true)
        private Long loginUserId; // 클라이언트에게 받는 값 X

        private Long goodsId;
        private int bidAmount;
    }
}
