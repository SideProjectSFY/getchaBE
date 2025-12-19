package com.ssafy.backend.goods.model;

import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GoodsResponseDto {

    @Schema(description = "굿즈 등록 시 반환할 굿즈Id")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddGoodsResult {

        private Long goodsId;
    }

    @Schema(description = "굿즈 목록 조회 시 하나의 Goods카드 데이터")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoodsCard {

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
    }


    @Schema(description = "굿즈 상세 조회 시 Goods 포함 다른 리스트 데이터")
    @Getter
    @Builder
    public static class GoodsDetailAll {
        private GoodsDetail goodDetail;
        private List<GoodsDetailImage> imageList;
        private List<BidParticipant> participants;
    }

    @Schema(description = "굿즈 상세 조회 시 굿즈관련데이터 위주")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoodsDetail {
        private Long goodsId;
        private Long animeId;

        private Long sellerId;
        private String sellerNickName;
        private String sellerProfileFilePath;

        private Category category;              // enum
        private String animeTitle;
        private String animePosterUrl;

        private String title;
        private String description;

        private Integer startPrice;
        private Integer currentBidAmount;
        private Integer instantBuyPrice;
        private AuctionStatus auctionStatus;    // enum

        private Integer duration;
        private LocalDateTime createdAt;
        private LocalDateTime auctionEndAt;
        private int wishCount;

        @Schema(name = "checkWish", description = "로그인한 사용자가 찜하기를 눌렀는지 체크하는 컬럼")
        private boolean checkWish;
        @Schema(name = "checkSeller", description = "로그인한 사용자가 작성한 글인지 체크하는 컬럼")
        private boolean checkSeller;
    }

    @Schema(description = "굿즈 상세 조회 시 굿즈이미지 데이터 위주")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoodsDetailImage {
        private Long imageId;
        private Long goodsId;
        private String filePath;
        private Integer sortOrder;
    }

    @Schema(description = "입찰 참여자 목록 조회용 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BidParticipant {

        private Integer bidRank;
        private Long bidId;
        private Long bidderId;
        private String bidderNickName;
        private String bidderProfileFilePath;
        private Integer bidAmount;
        private boolean isHighest;
    }


    @Schema(description = "사용자가 등록한 굿즈 목록 조회 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageInRegisteredGoodsCard {
        private Long goodsId;
        private String title;                   // 굿즈 글의 제목
        private Category category;
        private AuctionStatus auctionStatus;
        private String mainFilePath;            // 굿즈대표이미지
        private String animeTitle;              // 애니메이션 제목
        private Integer currentBidAmount;       // 현재 입찰가
        private Integer startPrice;             // 시작가
    }

    @Schema(description = "사용자가 참여한 굿즈 목록 조회 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageInParticipatedGoodsCard {
        private Long goodsId;
        private String title;                   // 굿즈 글의 제목
        private Category category;
        private AuctionStatus auctionStatus;
        private String mainFilePath;            // 굿즈대표이미지
        private String animeTitle;              // 애니메이션 제목
        private Integer currentBidAmount;       // 현재 입찰가
        private Integer myBidAmount;            // 내가 입찰한 금액
    }
}
