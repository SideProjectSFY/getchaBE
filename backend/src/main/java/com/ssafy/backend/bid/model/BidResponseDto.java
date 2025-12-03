package com.ssafy.backend.bid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class BidResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BidParticipant {

        private Long bidId;
        private Long bidderId;
        private String bidderNickName;
        private String bidderProfileFilePath;
        private int bid_amount;
        private boolean isHighest;
    }
}
