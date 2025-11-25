package com.ssafy.backend.goods.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GoodsRequestDto {

    @Getter
    @Setter
    public static class GoodsRegister {
                                            
        private String title;
        private String description;
        private String category;
        private Long animeId;
        private int startPrice;
        private int instantBuyPrice;
        private int duration;
    }

    @Getter
    @Setter
    public static class GoodsModify {
        private Long goodsId;

        private String title;
        private String description;
        private String category;
        private Long animeId;
        private int startPrice;
        private int instantBuyPrice;
        private int duration;
        private String auctionStatus;
    }

}
