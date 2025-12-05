package com.ssafy.backend.bid.model;

import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.goods.model.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BidMapper {


    /**
     * 굿즈 글에 대한 금액 및 입찰자 정보 조회 (For update)
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
     * 금액관련 상태 업데이트
     * @param coinWalletBalance 입찰금액 및 이전 최고 입찰자의 정보
     * @return 금액 업데이트 결과 반환
     */
    int updateBalanceStatus(BidInternalDto.CoinWalletBalance coinWalletBalance);

    /**
     * 거래 내역 등록할 정보
     * @param walletHistoryAndUserId 거래 내역 정보
     * @return 등록 결과 반환
     */
    int insertWalletHistory(BidInternalDto.WalletHistoryAndUserId walletHistoryAndUserId);

    /**
     * 지갑 잔액 조회 (FOR UPDATE)
     * @param loginUserId 로그인한 사용자Id(pk)
     * @return 잔액 조회
     */
    Integer selectCoinWalletBalanceForUpdate(Long loginUserId);

    /**
     * 등록할 입찰 데이터
     * @param bid 입찰 데이터
     * @return 등록 결과 반환
     */
    int insertCurrentBidAmount(Bid bid);

    /**
     * 경매 종료 대상 조회
     * @return 경매 종료 대상 리스트 반환
     */
    List<Goods> selectEndedAuctions();

    /**
     * 굿즈 경매 상태 업데이트
     * @param goodsId 굿즈ID(pk)
     * @return 굿즈 경매 상태 업데이트 결과반환
     * */
    int updateAuctionStatus(Long goodsId, AuctionStatus auctionStatus);
}
