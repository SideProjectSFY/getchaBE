package com.ssafy.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.NoSuchElementException;

@Getter
@AllArgsConstructor
public enum AuctionStatus {

    WAIT,
    PROCEEDING,
    COMPLETED,
    STOPPED;
    
//    private final String auctionStatus;

    /**
     * 프론트에서 던져준 파라미터를 Enum 타입으로 바꿔 반환하는 메서드
     * @param auctionStatus  문자열타입
     * @return AuctionStatus Enum타입
     */
    /*public static AuctionStatus getAuctionStatus(String auctionStatus) {

        if(auctionStatus == null) throw new NoSuchElementException("해당 경매 상태는 존재하지 않습니다.");

        for (AuctionStatus as : AuctionStatus.values()) {
            if (as.auctionStatus.equals(auctionStatus)) {
                return as;
            }
            else throw new NoSuchElementException("해당 경매 상태는 존재하지 않습니다.");
        }

        return null;
    }*/

}
