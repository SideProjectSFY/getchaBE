package com.ssafy.backend.wallet.model;


import com.ssafy.backend.common.PageRequest;
import com.ssafy.backend.common.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
public class WalletRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SearchWalletHistory extends PageRequest {
        private String startDate;
        private String endDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ChargeCoinAmount {

        @NotNull(message = "충전금액은 필수값입니다.")
        private Integer coinAmount;
    }

    @Schema(description = "충전 거래 내역 등록 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeWalletHistory {

        private Long userId;
        private TransactionType transactionType;
        private Integer amount;
        private String description;
    }
}
