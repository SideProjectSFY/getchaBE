package com.ssafy.backend.bid.service;

import com.ssafy.backend.bid.model.BidRequestDto;
import com.ssafy.backend.bid.model.BidResponseDto;

import java.util.List;

public interface BidService {

    /**
     * 굿즈 입찰 등록 (일반 + 즉시 구매 포함)
     * @param bidRegister 입찰 데이터
     */
    void postBidForGoods(BidRequestDto.BidRegister bidRegister);

    /**
     * 입찰에 참여한 참여자리스트 조회
     * @param goodsId 굿즈ID(pk)
     * @return 참여자리스트 반환
     */
    List<BidResponseDto.BidParticipant> getAllParticipant(Long goodsId);
}
