package com.ssafy.backend.user.service;

import com.ssafy.backend.goods.model.GoodsResponseDto;
import com.ssafy.backend.user.model.UserResponseDto;
import com.ssafy.backend.user.model.UserRequestDto;
import com.ssafy.backend.wish.model.WishResponseDto;

import java.util.List;

public interface UserService {

    /**
     * 내 프로필 조회
     */
    UserResponseDto getMyProfile(Long userId);

    /**
     * 내 프로필 수정
     */
    UserResponseDto updateMyProfile(Long userId, UserRequestDto request);

    /**
     * 회원 탈퇴
     */
    void deleteMyAccount(Long userId);

    /**
     * 사용자가 등록한 굿즈 카드 목록 조회
     * @param loginUserId 사용자ID(pk)
     * @return 사용자가 등록한 굿즈 카드 목록 리스트
     */
    List<GoodsResponseDto.MyPageInRegisteredGoodsCard> getAllRegisteredGoods(Long loginUserId);

    /**
     * 사용자가 참여한 굿즈 카드 목록 조회
     * @param loginUserId 사용자ID(pk)
     * @return 사용자가 등록한 굿즈 카드 목록 리스트
     */
    List<GoodsResponseDto.MyPageInParticipatedGoodsCard> getAllParticipatedGoods(Long loginUserId);

    /**
     * 마이페이지 내 사용자가 찜한 굿즈 목록 조회
     * @param loginUserId 사용자Id(pk)
     * @return 사용자가 찜한 굿즈 목록 리스트
     */
    List<WishResponseDto.WishedGoodsAll> getAllWishedGoods(Long loginUserId);

}
