package com.ssafy.backend.goods.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Goods 테이블의 컬럼과 거의 똑같이 사용하는 Model
 * */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    private Long id;
    private Long sellerId;
    private Long animeId;
    // TODO : category enum 생성 필요
    private String title;
    private String description;
    private int startPrice;
    private int instantBuyPrice;
    // TODO : auctionStatus enum 생성 필요
    private int duration;
    private LocalDateTime auctionEndAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}
