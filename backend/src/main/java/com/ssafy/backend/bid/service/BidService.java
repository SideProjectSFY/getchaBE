package com.ssafy.backend.bid.service;

import com.ssafy.backend.bid.model.BidRequestDto;

public interface BidService {

    /**
     * 굿즈 입찰 등록 (일반 + 즉시 구매 포함)
     * @param bidRegister 입찰 데이터
     */
    void postBidForGoods(Long loginUserId, BidRequestDto.BidRegister bidRegister);

    /**
     * 굿즈 경매 상태 업데이트
     * @param goodsId 굿즈ID(pk)
     */
    void updateStopAuctionStatus(Long loginUserId, Long goodsId);
}
