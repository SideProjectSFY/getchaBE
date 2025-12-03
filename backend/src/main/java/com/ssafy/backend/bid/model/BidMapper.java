package com.ssafy.backend.bid.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BidMapper {

    /**
     * 경매에 입찰한 참여자 리스트 조회
     * @param goodsId 굿즈Id(pk)
     * @return 경매 입찰자 리스트 결과 반환
     */
    List<BidResponseDto.BidParticipant> selectBidParticipantByGoodsId(Long goodsId);

    /**
     * 굿즈 글에 대한 금액 및 입찰자 정보 조회
     * @param goodsId 굿즈Id(pk)
     * @return 굿즈 글에 대한 금액 및 입찰자 정보 결과 반환
     */
    BidInternalDto.GoodsPriceBidInfo selectGoodsPriceAndBidInfoByGoodsId(Long goodsId);

    /**
     * 현재 최고가 여부 false 로 업데이트
     * @param goodsId 굿즈Id(pk)
     * @return 여부 업데이트 결과반환
     */
    int updateIsHighestByGoodsId(Long goodsId);

    /**
     * 예치금 unlock 로 업데이트
     * @param coinWalletBalance 입찰금액 및 이전 최고 입찰자의 정보
     * @return 예치금 업데이트 결과 반환
     */
    int updateToUnlockBidAmount(BidInternalDto.CoinWalletBalance coinWalletBalance);

    /**
     * 예치금 lock 로 업데이트
     * @param coinWalletBalance 입찰금액 및 로그인한 사용자 정보
     * @return 예치금 업데이트 결과 반환
     */
    int updateToLockBidAmount(BidInternalDto.CoinWalletBalance coinWalletBalance);

    /**
     * 거래 내역 등록할 정보
     * @param walletHistoryAndUserId 거래 내역 정보
     * @return 등록 결과 반환
     */
    int insertWalletHistory(BidInternalDto.WalletHistoryAndUserId walletHistoryAndUserId);

    /**
     * 등록할 입찰 데이터
     * @param bid 입찰 데이터
     * @return 등록 결과 반환
     */
    int insertCurrentBidAmount(Bid bid);

}
