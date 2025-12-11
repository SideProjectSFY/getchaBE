package com.ssafy.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuctionStatus {

    WAIT,
    PROCEEDING,
    COMPLETED,
    STOPPED;

}
