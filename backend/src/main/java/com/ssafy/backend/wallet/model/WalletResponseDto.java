package com.ssafy.backend.wallet.model;

import com.ssafy.backend.common.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WalletResponseDto {

    @Getter
    @Builder
    public static class CoinWalletStatus {
        private int balance;
        private int lockedBalance;
    }

    @Getter
    @Builder
    public static class WalletHistoryAll {
        private Long id;
        private Long goodsId;
        private TransactionType transactionType;
        private Integer amount;
        private String description;
        private LocalDateTime createdAt;
    }
}
