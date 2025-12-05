package com.ssafy.backend.bid.model;

import com.ssafy.backend.common.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  BidInternalDto : 내부 비즈니스 DTO
 *  - 응답 / 요청 DTO 와는 다르게 내부에서만 사용되는 DTO
 */

@Getter
public class BidInternalDto {

    @Schema(description = "굿즈가격 및 입찰정보 조회 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoodsPriceBidInfo {

        private Long sellerId;
        private Integer startPrice;
        private Integer instantBuyPrice;
        private Long bidId;
        private Long bidderId;
        private Integer currentBidAmount;          // 현재 최고가 금액

    }

    @Schema(description = "가상화폐 예치금 Lock/Unlock DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoinWalletBalance {

        @Schema(name = "userId", description = "로그인한 사용자Id(pk) or 이전 최고입찰자Id(pk)")
        private Long userId;
        private int bidAmount;

        @Schema(description = "금액처리상태")
        private TransactionType balanceStatus;
    }

    @Schema(description = "거래 내역 등록 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletHistoryAndUserId {

        @Schema(name = "userId", description = "로그인한 사용자Id(pk) or 이전 최고입찰자Id(pk)")
        private Long userId;

        private Long goodsId;
        private TransactionType transactionType;
        private int amount;
        private String description;
    }
}
