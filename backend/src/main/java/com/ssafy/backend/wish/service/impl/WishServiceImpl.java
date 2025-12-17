package com.ssafy.backend.wish.service.impl;

import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.goods.model.GoodsMapper;
import com.ssafy.backend.wish.model.Wish;
import com.ssafy.backend.wish.model.WishMapper;
import com.ssafy.backend.wish.model.WishResponseDto;
import com.ssafy.backend.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WishServiceImpl implements WishService {

    private final WishMapper wishMapper;
    private final GoodsMapper goodsMapper;

    @Override
    @Transactional
    public WishResponseDto.AddWishResult addWish(Long goodsId, Long loginUserId) {

        // 굿즈 존재 여부 체크
        if (!goodsMapper.existsActiveGoodsByUserId(loginUserId, goodsId)) {
            throw new NoSuchElementException("존재하지 않는 굿즈입니다.");
        }

        // 본인 글 찜 금지
        Long sellerId = goodsMapper.selectSellerIdByGoodsId(goodsId);
        if (sellerId.equals(loginUserId)) {
            throw new AccessDeniedException("본인의 굿즈는 찜할 수 없습니다.");
        }

        Wish wish = Wish.builder()
                .goodsId(goodsId)
                .userId(loginUserId)
                .build();

        try {
            int insertWish = wishMapper.insertWish(wish);
            if(insertWish < 1)
                throw new CustomException("찜 등록하는 것에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (DuplicateKeyException e) {
            throw new CustomException("이미 찜한 굿즈입니다.", HttpStatus.CONFLICT);
        }

        return WishResponseDto.AddWishResult.builder()
                .wishId(wish.getId())
                .checkWish(true)
                .wishCount(wishMapper.selectWishCount(goodsId))
                .build();
    }

    @Override
    @Transactional
    public WishResponseDto.DeleteWishResult deleteWish(Long goodsId, Long loginUserId) {

        // 굿즈 존재 여부 체크
        if (!goodsMapper.existsActiveGoodsByUserId(loginUserId, goodsId)) {
            throw new NoSuchElementException("존재하지 않는 굿즈입니다.");
        }

        Wish wish = Wish.builder()
                .goodsId(goodsId)
                .userId(loginUserId)
                .build();

        int deleteWish = wishMapper.deleteWish(wish);
        if(deleteWish < 1)
            throw new CustomException("찜 취소하는 것에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR);

        return WishResponseDto.DeleteWishResult.builder()
                .wishCount(wishMapper.selectWishCount(goodsId))
                .checkWish(false)
                .build();

    }
}
