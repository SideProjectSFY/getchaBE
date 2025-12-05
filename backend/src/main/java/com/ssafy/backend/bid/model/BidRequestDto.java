package com.ssafy.backend.bid.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

        @NotNull(message = "굿즈ID 는 필수값입니다.")
        private Long goodsId;

        @NotNull(message = "입찰금액은 필수값입니다.")
        private Integer bidAmount;
    }
}
