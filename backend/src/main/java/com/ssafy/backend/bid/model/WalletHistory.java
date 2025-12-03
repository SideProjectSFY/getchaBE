package com.ssafy.backend.bid.model;

import com.ssafy.backend.common.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WalletHistory {

    private Long id;
    private Long walletId;
    private Long goodsId;
    private TransactionType transactionType;
    private int amount;
    private String description;
    private LocalDateTime createdAt;
}
