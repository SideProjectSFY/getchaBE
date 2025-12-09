package com.ssafy.backend.wallet.service;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.wallet.model.WalletRequestDto;
import com.ssafy.backend.wallet.model.WalletResponseDto;

public interface WalletService {

    /**
     * 거래 내역 목록 조회
     * @param loginUserId 로그인 사용자ID(pk)
     * @param searchHistory 거래내역 기간 검색 및 페이징 관련 데이터
     * @return 거래내역 목록 반환
     */
    PageResponse<WalletResponseDto.WalletHistoryAll> getAllWalletHistory(
            Long loginUserId, WalletRequestDto.SearchWalletHistory searchHistory);

    /**
     * 자산 현황 조회
     * @param loginUserId 로그인 사용자ID(pk)
     * @return 잔액, 예치금
     */
    WalletResponseDto.CoinWalletStatus getCoinWalletStatus(Long loginUserId);


    /**
     * 회원가입시 가상화폐지갑 생성
     * @param loginUserId 사용자Id(pk)
     */
    void createCoinWallet(Long loginUserId);

    /**
     * 코인 충전 (임시)
     * @param loginUserId 로그인 사용자ID(pk)
     * @param chargeCoinAmount 충전할 돈(코인)
     */
    void chargeCoin(Long loginUserId, WalletRequestDto.ChargeCoinAmount chargeCoinAmount);
}
