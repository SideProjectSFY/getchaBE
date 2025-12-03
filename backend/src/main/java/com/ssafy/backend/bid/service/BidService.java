package com.ssafy.backend.bid.service;

import com.ssafy.backend.bid.model.BidRequestDto;
import com.ssafy.backend.bid.model.BidResponseDto;

import java.util.List;

public interface BidService {

    void postBidForGoods(BidRequestDto.BidRegister bidRegister);

    List<BidResponseDto.BidParticipant> getAllParticipant(Long goodsId);
}
