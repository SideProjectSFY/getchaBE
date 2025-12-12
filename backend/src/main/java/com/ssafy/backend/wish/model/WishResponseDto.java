package com.ssafy.backend.wish.model;

import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class WishResponseDto {

    @Schema(description = "Wish 등록 시 반환할 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddWishResult {
        private Long wishId;
        private boolean checkWish;
    }

    @Schema(description = "Wish 삭제 시 반환할 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteWishResult {
        private boolean checkWish;
    }

    @Schema(description = "사용자가 찜한 굿즈 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishedGoodsAll {
        private Long wishId;
        private Long goodsId;
        private String title;
        private Category category;
        private AuctionStatus auctionStatus;
        private Integer startPrice;
        private String animeTitle;
        private Integer currentBidAmount;
        private String mainFilePath;
        private LocalDateTime wishedAt;
    }

    @Schema(description = "굿즈 목록 조회 시 하나의 Goods카드 데이터")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopGoodsCard {

        private Long goodsId;
        private Long sellerId;
        private String sellerNickname;          // 판매자닉네임
        private int wishCount;                  // 찜 수
        private Category category;
        private String mainFilePath;            // 굿즈대표이미지
        private String title;                   // 굿즈 글의 제목
        private String animeTitle;              // 애니메이션 제목
        private Integer currentBidAmount;       // 현재 입찰가
        private Integer startPrice;             // 시작가
        private AuctionStatus auctionStatus;
        private LocalDateTime auctionEndAt;
        private LocalDateTime createdAt;
    }
}
