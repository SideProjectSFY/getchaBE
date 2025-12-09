package com.ssafy.backend.wallet.model;

import com.ssafy.backend.common.enums.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * WalletHistory 테이블의 컬럼과 거의 똑같이 사용하는 Model
 * */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletHistory {

    private Long id;
    private Long walletId;
    private Long goodsId;
    private TransactionType transactionType;
    private Integer amount;
    private String description;
    private LocalDateTime createdAt;
}
