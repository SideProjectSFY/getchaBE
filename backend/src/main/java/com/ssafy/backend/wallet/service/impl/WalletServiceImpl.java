package com.ssafy.backend.wallet.service.impl;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.wallet.model.WalletHistory;
import com.ssafy.backend.wallet.model.WalletMapper;
import com.ssafy.backend.wallet.model.WalletResponseDto;
import com.ssafy.backend.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletMapper walletMapper;
    private static final int SIGN_UP_COIN = 50_000;

    @Override
    public PageResponse<WalletHistory> getAllWalletHistory(Long loginUserId) {

        // TODO : 월 단위 검색을 넣을지 말지 고민

        // 데이터 조회
        List<WalletHistory> walletHistoryList = Optional.ofNullable(walletMapper.selectWalletHistory(loginUserId))
                .orElse(Collections.emptyList());

        long totalCount = walletMapper.countWalletHistory(loginUserId);
        // 거래 내역 글이 0개 인 경우
        if(totalCount == 0) {
//            return new PageResponse<>(Collections.emptyList(), page, 0, 0);
        }

//        int totalPages = (int) Math.ceil((double) totalCount / size);

        // 총 페이지 수 보다 페이지 값이 크게 들어왔을 경우, 빈 리스트 보여주기
//        if (page > totalPages) {
//            return new PageResponse<>(Collections.emptyList(), page, totalPages, totalCount);
//        }

//        return new PageResponse<>(goodsCardsList, page, totalPages, totalCount);
        return null;
    }

    @Override
    public WalletResponseDto.CoinWalletStatus getCoinWalletStatus(Long loginUserId) {

        WalletResponseDto.CoinWalletStatus coinWalletStatus = walletMapper.selectCoinWallet(loginUserId);
        if(coinWalletStatus == null) {
            throw new NoSuchElementException("존재하지 않는 유저 또는 지갑이 없습니다.");
        }

        int balance = coinWalletStatus.getBalance();
        int lockedBalance = coinWalletStatus.getLockedBalance();

        if(balance < 0 || lockedBalance < 0) {
            log.warn("지갑 조회 - 잔액 음수 발생!");
            log.warn("balance : {}", balance);
            log.warn("lockedBalance  : {}", lockedBalance);
        }

        return coinWalletStatus;
    }

    @Override
    @Transactional
    public void createCoinWallet(Long loginUserId) {
        // 지갑 생성
        int createResult = walletMapper.createCoinWallet(loginUserId);
        if(createResult < 1)
            throw new CustomException("지갑 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

        // 5만원 충전
        chargeCoin(loginUserId, SIGN_UP_COIN);
    }


    @Override
    @Transactional
    public void chargeCoin(Long loginUserId, Integer coinAmount) {

        if(coinAmount < 0)
            throw new CustomException("충전금액은 양수여야 합니다.", HttpStatus.BAD_REQUEST);

        int insertCoinResult = walletMapper.chargeCoin(loginUserId, coinAmount);
        if(insertCoinResult < 1)
            throw new CustomException("코인 충전에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

        log.info("coinWallet 에 {} 원 충전되었습니다.", coinAmount);
    }

}
