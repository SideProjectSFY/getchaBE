package com.ssafy.backend.goods.model;

import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.enums.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Goods 조회용 객체
 * */

@Getter
public class GoodsVO {

    /**
     * GoodsDetailAllVO : 굿즈 상세 조회 시 Goods 포함 다른 리스트 데이터
     * */
    @Getter
    @Builder
    public static class GoodsDetailAllVO {
        GoodsDetailVO goodDetailVO;

        // TODO : 추후 추가할 데이터
//        private SellerVO seller;
//        private AuctionVO auction;
//        private List<AuctionParticipantVO> participants;
//        private List<CommentVO> comments;
    }

    /**
     * GoodsDetailVO : 굿즈 상세 조회 시 굿즈관련데이터 위주
     * */
    @Getter
    @Builder
    public static class GoodsDetailVO {
        private Long goodsId;
        private Long sellerId;
        private String sellerNickName;
        private int wishCount;
        private Long animeId;
        private String animeTitle;
        private Category category;              // enum
        private String title;
        private String description;
        private int startPrice;
        private int instantBuyPrice;
        private AuctionStatus auctionStatus;    // enum
        private int duration;
        private LocalDateTime auctionEndAt;
        private LocalDateTime createdAt;
    }
}
