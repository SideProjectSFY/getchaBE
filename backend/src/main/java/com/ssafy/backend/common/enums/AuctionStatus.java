package com.ssafy.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuctionStatus {

    WAIT("경매 대기"),
    PROCEEDING("경매 진행중"),
    COMPLETED("낙찰"),
    STOPPED("패찰");
    
    private final String auctionStatus;

}
