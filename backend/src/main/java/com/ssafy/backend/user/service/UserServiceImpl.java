package com.ssafy.backend.user.service;

import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.goods.model.GoodsMapper;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import com.ssafy.backend.user.model.*;
import com.ssafy.backend.wallet.model.WalletMapper;
import com.ssafy.backend.wish.model.WishMapper;
import com.ssafy.backend.wish.model.WishResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final GoodsMapper goodsMapper;
    private final WalletMapper walletMapper;
    private final WishMapper wishMapper;

    /**
     * 내 프로필 조회
     */
    @Override
    public UserResponseDto getMyProfile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null || user.getDeletedAt() != null) {
            throw new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        List<AnimeSelectionDto> likedAnimes = userMapper.findUserLikedAnimes(userId);

        return UserResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .accountNum(user.getAccountNum())
                .accountBank(user.getAccountBank())
                .likedAnimes(likedAnimes)
                .build();
    }

    /**
     * 내 프로필 수정
     */
    @Override
    @Transactional
    public UserResponseDto updateMyProfile(Long userId, UserRequestDto request) {

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        validateLikedAnimeIds(request);

        User updatePayload = User.builder()
                .id(userId)
                .nickname(request.getNickname())
                .accountNum(request.getAccountNum())
                .accountBank(request.getAccountBank())
                .likedAnimeId1(request.getLikedAnimeId1())
                .likedAnimeId2(request.getLikedAnimeId2())
                .likedAnimeId3(request.getLikedAnimeId3())
                .build();

        int updateUser = userMapper.updateUser(updatePayload);
        if(updateUser < 1) throw new CustomException("프로필 수정에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


        List<AnimeSelectionDto> likedAnimes = userMapper.findUserLikedAnimes(userId);

        return UserResponseDto.builder()
                .id(userId)
                .nickname(request.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .accountNum(request.getAccountNum())
                .accountBank(request.getAccountBank())
                .likedAnimes(likedAnimes)
                .build();
    }

    private void validateLikedAnimeIds(UserRequestDto request) {
        if (request.getLikedAnimeId1() == null
                || request.getLikedAnimeId2() == null
                || request.getLikedAnimeId3() == null) {
            throw new CustomException("관심 애니메이션 3개를 모두 선택해주세요.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 회원 탈퇴
     */
    @Override
    @Transactional
    public void deleteMyAccount(Long userId) {

        // 1. 등록한 진행중인 상태의 굿즈가 있을 경우
        boolean existGoodsResult = goodsMapper.existsActiveGoodsByUserId(userId, null);
        //1-1. 'PROCEEDING'(진행중) 인 경우 삭제 불가
        if(existGoodsResult) {
            throw new CustomException("진행 중인 경매가 있어 회원 탈퇴를 할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. 진행중 상태가 아닌 등록한 굿즈 글이 존재하는지 확인
        int existGoodsResultNotProceeding = goodsMapper.existsGoodsByUserId(userId);
        if(existGoodsResultNotProceeding >= 1) {
            // 등록한 글이 있는 경우
            // 2-1. 모든 굿즈 글이 'PROCEEDING'(진행중) 이 아닌 경우 삭제
            int deleteResult = goodsMapper.deleteGoods(null, userId);
            if(deleteResult < 1) throw new CustomException("굿즈 글 삭제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // 3. 예치금이 0원일 경우에만 삭제 가능
        int lockedBalance = walletMapper.selectCoinWallet(userId).getLockedBalance();
        if(lockedBalance != 0) throw new CustomException("진행 중인 경매가 있어 회원 탈퇴를 할 수 없습니다.", HttpStatus.BAD_REQUEST);

        int deleteUser = userMapper.deleteUser(userId);
        if(deleteUser < 1) throw new CustomException("회원 탈퇴에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 마이페이지 내 사용자가 등록한 굿즈 목록 조회
     */
    @Override
    public List<GoodsResponseDto.MyPageInRegisteredGoodsCard> getAllRegisteredGoods(Long loginUserId) {
        // 데이터 조회 후, 없으면 빈 리스트 던지기
        return Optional.ofNullable(goodsMapper.selectAllRegisteredGoods(loginUserId))
                .orElse(Collections.emptyList());
    }

    /**
     * 마이페이지 내 사용자가 참여한 굿즈 목록 조회
     */
    @Override
    public List<GoodsResponseDto.MyPageInParticipatedGoodsCard> getAllParticipatedGoods(Long loginUserId) {
        // 데이터 조회 후, 없으면 빈 리스트 던지기
        return Optional.ofNullable(goodsMapper.selectAllParticipatedGoods(loginUserId))
                .orElse(Collections.emptyList());
    }

    /**
    * 마이페이지 내 사용자가 찜한 굿즈 목록 조회
    * */
    @Override
    public List<WishResponseDto.WishedGoodsAll> getAllWishedGoods(Long loginUserId) {
        // 데이터 조회 후, 없으면 빈 리스트 던지기
        return Optional.ofNullable(wishMapper.selectAllWishedGoods(loginUserId))
                .orElse(Collections.emptyList());
    }
}
