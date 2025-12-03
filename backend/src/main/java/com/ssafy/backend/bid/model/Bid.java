package com.ssafy.backend.bid.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Bid {

    private Long id;
    private Long goods_id;
    private Long bidder_id;
    private int bid_amount;
    private boolean is_highest;
    private LocalDateTime created_at;
}
