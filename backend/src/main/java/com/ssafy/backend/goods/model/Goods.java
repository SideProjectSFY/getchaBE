package com.ssafy.backend.goods.model;

import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.enums.Category;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Goods 테이블의 컬럼과 거의 똑같이 사용하는 Model
 * */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Goods {

    private Long id;
    private Long sellerId;
    private Long animeId;
    private Category category;              // enum
    private String title;
    private String description;
    private int startPrice;
    private int instantBuyPrice;
    private AuctionStatus auctionStatus;    // enum
    private int duration;
    private LocalDateTime auctionEndAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}
