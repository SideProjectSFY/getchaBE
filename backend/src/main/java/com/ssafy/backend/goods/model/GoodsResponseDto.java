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



    /**
     * GoodsCard : 굿즈 목록 조회 시 하나의 Goods카드 데이터
     * */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoodsCard {
        private Long goodsId;
        private Long sellerId;
        private String sellerNickname;          // 판매자닉네임
        private int wishCount;                  // 찜 수
        private Category category;
        private String mainFilePath;            // 굿즈대표이미지
        private String title;                   // 굿즈 글의 제목
        private String animeTitle;              // 애니메이션 제목
        private int currentBidAmount;           // 현재 입찰가
        private AuctionStatus auctionStatus;
        private LocalDateTime auctionEndAt;
        private LocalDateTime createdAt;
    }

    /**
     * GoodsDetailAll : 굿즈 상세 조회 시 Goods 포함 다른 리스트 데이터
     * */
    @Getter
    @Builder
    public static class GoodsDetailAll {
        GoodsDetail goodDetail;
        List<GoodsDetailImage> imageList;

        // TODO : 추후 추가할 데이터
//        private AuctionVO auction;
//        private List<AuctionParticipantVO> participants;
    }

    /**
     * GoodsDetail : 굿즈 상세 조회 시 굿즈관련데이터 위주
     * */
    @Getter
    @Builder
    public static class GoodsDetail {
        private Long goodsId;
        private Long animeId;

        private Long sellerId;
        private String sellerNickName;
        private String sellerProfileFilePath;

        private Category category;              // enum
        private String animeTitle;

        private String title;
        private String description;

        private int startPrice;
        private int currentBidAmount;
        private int instantBuyPrice;
        private AuctionStatus auctionStatus;    // enum

        private LocalDateTime createdAt;
        private LocalDateTime auctionEndAt;
        private int wishCount;

        @Schema(name = "isWish", description = "로그인한 사용자가 찜하기를 눌렀는지 체크하는 컬럼")
        private boolean isWish;
        @Schema(name = "isSeller", description = "로그인한 사용자가 작성한 글인지 체크하는 컬럼")
        private boolean isSeller;
    }

    /**
     *  GoodsDetailImage : 굿즈 상세 조회 시 굿즈이미지 데이터 위주
     */
    @Getter
    @Builder
    public static class GoodsDetailImage {
        private Long id;
        private Long goods_id;
        private String file_path;
        private int sort_order;
    }
}
