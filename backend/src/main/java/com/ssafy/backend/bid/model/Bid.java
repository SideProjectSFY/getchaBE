package com.ssafy.backend.bid.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Bid {

    private Long id;
    private Long goodsId;
    private Long bidderId;
    private int bidAmount;
    private boolean isHighest;
    private LocalDateTime createdAt;
}
