package com.ssafy.backend.wish.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WishMapper {

    /**
     * 찜 하기
     * @param wish 찜 데이터
     * @return 찜 하기 결과 반환
     */
    int insertWish(Wish wish);

    /**
     * 찜 취소
     * @param wish 찜 데이터
     * @return 찜 취소 결과 반환
     */
    int deleteWish(Wish wish);

    /**
     * 마이페이지 내 사용자가 찜한 굿즈 목록 조회
     * @param loginUserId 로그인한 사용자Id(pk)
     * @return 찜한 굿즈 목록 조회
     */
    List<WishResponseDto.WishedGoodsAll> selectAllWishedGoods(Long loginUserId);

    /**
     * 찜 기준 인기 굿즈 조회
     * @return 인기 굿즈 조회
     */
    List<WishResponseDto.TopGoodsCard> selectTop6GoodsOnWishCount();


}
