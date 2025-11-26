package com.ssafy.backend.goods.model;

import com.ssafy.backend.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
public class GoodsRequestDto {

    @Schema(description = "굿즈 등록 요청 DTO")
    @Getter
    @Setter
    public static class GoodsRegister {

        @Schema(description = "애니ID(pk)")
        private Long animeId;
        @Schema(description = "카테고리", example = "figure")
        private String category;
        private String title;
        private String description;
        @Schema(description = "시작가")
        private int startPrice;
        @Schema(description = "즉시구매가")
        private int instantBuyPrice;
        @Schema(description = "경매기간(일)", example = "3", defaultValue = "3")
        private int duration;
    }

    @Schema(description = "굿즈 목록 조회 DTO")
    @Getter
    @Setter
    public static class GoodsLookUp extends PageRequest {
        @Schema(description = "경매상태", example = "wait")
        private String auctionStatus;
        @Schema(description = "카테고리", example = "figure")
        private String category;
    }

    @Getter
    @Setter
    public static class GoodsModify {
        private Long goodsId;

        private String title;
        private Long animeId;
        private String category;
        private String description;
        private int startPrice;
        private int instantBuyPrice;
        private int duration;
        private String auctionStatus;
    }

}
