package com.ssafy.backend.ai.model;

import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class RecommendedResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedGoods {

        private Long goodsId;
        private Long sellerId;
        private String sellerNickname;          // 판매자닉네임
        private int wishCount;                  // 찜 수
        private boolean checkWish;              // 로그인 사용자가 찜했는지 체크
        private Category category;
        private String mainFilePath;            // 굿즈대표이미지
        private String title;                   // 굿즈 글의 제목
        private String animeTitle;              // 애니메이션 제목
        private Integer currentBidAmount;       // 현재 입찰가
        private Integer startPrice;             // 시작가
        private AuctionStatus auctionStatus;
        private LocalDateTime auctionEndAt;
        private LocalDateTime createdAt;

        private Long animeId;
        private Double matchRate;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedAnime {

        private Long animeId;
        private Double matchRate;

        private String animeTitle;
        private String posterUrl;
    }

}
