package com.ssafy.backend.wish.service;

import com.ssafy.backend.wish.model.WishResponseDto;


public interface WishService {

    // 찜 등록
    WishResponseDto.AddWishResult addWish(Long goodsId, Long loginUserId);

    // 찜 취소
    WishResponseDto.DeleteWishResult deleteWish(Long goodsId, Long loginUserId);

}
