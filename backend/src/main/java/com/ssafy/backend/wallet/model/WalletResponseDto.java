package com.ssafy.backend.wallet.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WalletResponseDto {

    @Getter
    @Builder
    public static class CoinWalletStatus {
        private int balance;
        private int lockedBalance;
    }
}
