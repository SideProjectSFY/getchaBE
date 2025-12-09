package com.ssafy.backend.wallet.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WalletMapper {

    /**
     * 거래 내역 목록 조회
     * @param loginUserId 사용자ID(pk)
     * @return 목록 반환
     */
    List<WalletHistory> selectWalletHistory(Long loginUserId);

    /**
     * 거래 내역 총 개수
     * @param loginUserId 사용자ID(pk)
     * @return 총 개수 반환
     */
    long countWalletHistory(Long loginUserId);

    /**
     * 자산 현황 조회
     * @param loginUserId 사용자ID(pk)
     * @return 자산 반환
     */
    WalletResponseDto.CoinWalletStatus selectCoinWallet(Long loginUserId);

    /**
     * 지갑 생성
     * @param loginUserId 사용자Id(pk)
     * @return
     */
    int createCoinWallet(Long loginUserId);

    /**
     * 코인 충전 (임시)
     * @param loginUserId 사용자ID(pk)
     * @param coinAmount 충전할 돈 (코인)
     * @return 결과 반환
     */
    int chargeCoin(Long loginUserId, Integer coinAmount);
}
