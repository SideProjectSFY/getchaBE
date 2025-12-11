package com.ssafy.backend.bid.service.impl;

import com.ssafy.backend.bid.model.*;
import com.ssafy.backend.bid.service.BidService;
import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.enums.NotificationType;
import com.ssafy.backend.common.enums.TransactionType;
import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.goods.model.Goods;
import com.ssafy.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public  class BidServiceImpl implements BidService {

    private final BidMapper bidMapper;
    private final NotificationService notificationService;

    private static final int LIMIT_AMOUNT = 5_000_000;
    
    @Override
    @Transactional
    public void postBidForGoods(Long loginUserId, BidRequestDto.BidRegister bidRegister) {
        
        /*
        * <조건>
        * 1. 굿즈를 등록한 판매자는 입찰할 수 없음
        * 2. 본인이 현재 최고 입찰자인 상태일 땐, 다시 재입찰 못함
        * 3. 현재 최고 입찰가보다 높으면서, 500만원(제한금액) 이하인 금액만 입찰 가능
        * */

        /* <즉시구매가 이상인 경우>
         * 해당 굿즈 상태를 '낙찰' 로 변경
         *  - 입찰 금액이 없다면 bidId == null 내부에 있는 로직은 돌리지 않고
         * 1. 첫 입찰자를 바로 5번 기능으로 건너뛰어 입찰 등록한다.
         * 2. 그 후는 입찰 금액이 있다면의 5번까지 진행후 처리하는 로직을 따라가면 된다.
         *  - 입찰 금액이 있다면 5번까지 진행 후
         * 1. 새 입찰자의 입찰금을 lock 거는 것이 아닌 바로 coin_wallet 의 balence 에서 최고 입찰가 돈을 뺀다.
         * 2. wallet_history 에 (출금) 정산 완료 로 내역 기록
         * */

        Long goodsId = bidRegister.getGoodsId();
        Integer bidAmount = bidRegister.getBidAmount();

        // 굿즈 + 입찰 정보 조회
        BidInternalDto.GoodsPriceBidInfo info = bidMapper.selectGoodsPriceAndBidInfoByGoodsId(bidRegister.getGoodsId());
        if(info == null) throw new NoSuchElementException("존재하지 않는 굿즈 또는 이미 종료된 경매입니다.");

        Long sellerId = info.getSellerId();
        Long bidId = info.getBidId();                           // null 이면 첫 입찰
        Integer startPrice = info.getStartPrice();
        Integer currentBidAmount = info.getCurrentBidAmount();  // null 이면 첫 입찰
        Integer instantBuyPrice = info.getInstantBuyPrice();
        Long beforeBidderId = info.getBidderId();               // 기존 최고 입찰자
        String title = info.getTitle();

        boolean isFirstBid = (bidId == null);
        boolean isInstantBuy = (instantBuyPrice != null && bidAmount >= instantBuyPrice);

        /*
        * 0. 공통 기본 검증
        * */

        // 0-1. 판매자 입찰 제한
        if (Objects.equals(sellerId, loginUserId)) {
            throw new CustomException("판매자는 자신의 굿즈에 입찰할 수 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 0-2. 현재 최고 입찰자는 재입찰 불가
        if(beforeBidderId != null && Objects.equals(beforeBidderId, loginUserId))
            throw new CustomException("현재 최고 입찰자는 재입찰할 수 없습니다.", HttpStatus.BAD_REQUEST);

        /*
        * 1. 금액 검증 + 첫 입찰(일반) 처리
        * */

        // 첫입찰 인 경우
        if(isFirstBid) {
            // 즉시 구매가 미만(일반 입찰) 인 경우
            if(!isInstantBuy) {
                // 1-1. 시작가 이상, 제한금액 이하인지 체크
                if(startPrice == null || bidAmount < startPrice || bidAmount > LIMIT_AMOUNT)
                    throw new CustomException("시작가 이상, 제한금액(500만원) 이하일 때 입찰이 가능합니다.", HttpStatus.BAD_REQUEST);

                // 1-2. 첫 입찰 성공 -> 상태를 '경매 진행'으로 변경(WAIT -> PROCEEDING)
                updateAuctionStatusOrThrow(goodsId, AuctionStatus.PROCEEDING);

                //  이후 흐름은 5번(입찰등록)부터 공통 처리
            }
            // 첫입찰 + 즉시 구매가 이상인 경우 위 로직 안돌리고 바로 5번(입찰등록)부터 공통처리
        } else {
            // 기존 입찰이 있는 경우

            // 1-3 현재 최고 입찰가 이상, 제한금액 이하인지 체크
            if(bidAmount < currentBidAmount || bidAmount > LIMIT_AMOUNT) {
                throw new CustomException("현재 입찰가 이상, 제한금액(500만원) 이하일 때 입찰이 가능합니다.", HttpStatus.BAD_REQUEST);
            }

            /*
            * 2. 기존 최고 입찰 isHighest 여부 false 로 업데이트
            * */

            int updateIsHighestResult = bidMapper.updateIsHighestByGoodsId(goodsId);
            if(updateIsHighestResult < 1) throw new CustomException("현재 최고가 여부 업데이트에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            /*
            * 3. 기존 최고 입찰자 예치금 unlock + 지갑 내역 BIDUNLOCK 기록
            * */
            unlockBidAmount(beforeBidderId, goodsId, currentBidAmount);

            // ★ 구매자(기존 최고 입찰자) 입찰 실패 알림 ★
            notificationService.createNotification(
                beforeBidderId,
                    NotificationType.AUCTION_OUTBID,
                    Map.of("itemName", title),
                    goodsId
            );
            
        }

        // 0-3. 새 입찰자의 코인 잔액 검증 (LOCK 또는 PAY 가능 여부 확인)
        validateWalletBalance(loginUserId, bidAmount);


        /*
        * 4. 새로운 입찰 등록
        * */

        // 입찰로 등록할 bid 객체 빌드
        Bid bid = Bid.builder()
                .goodsId(goodsId)
                .bidderId(loginUserId)
                .bidAmount(bidAmount)
                .build();

        int insertCurrentBidAmountResult = bidMapper.insertCurrentBidAmount(bid);
        if(insertCurrentBidAmountResult < 1)
            throw new CustomException("입찰금액 등록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

        /*
        * <즉시 구매가 이상인 경우>
        * - 바로 낙찰 처리(상태 변경 + 지불/ 입금 정산)
        *
        * 5. 즉시구매 분기
        * */
        if(isInstantBuy) {

            // ★ 구매자(새로운 입찰자) 경매 즉시 구매 알림 ★
            notificationService.createNotification(
                    loginUserId,
                    NotificationType.AUCTION_BUY_NOW,
                    Map.of("itemName", title),
                    goodsId
            );
            
            // ★ 판매자(즉시구매가 입찰) 경매 즉시 구매 알림 ★
            notificationService.createNotification(
                    sellerId,
                    NotificationType.AUCTION_BUY_NOW,
                    Map.of("itemName", title),
                    goodsId
            );

            // 5-1. 굿즈 상태를 '낙찰' 로 변경
            updateAuctionStatusOrThrow(goodsId, AuctionStatus.COMPLETED);



            // 5-2. 새 입찰자의 금액을 Lock 이 아닌 실제 출금 처리 + 지갑에 "출금" 기록
            updateBalanceAndHistory(
                    loginUserId,
                    goodsId,
                    bidAmount,
                    TransactionType.EXPENSE, // balance - bidAmount
                    TransactionType.EXPENSE, // walletHistory 타입
                    "낙찰"
            );

            // ★ 구매자(새로운 입찰자) 낙찰 알림 ★
            notificationService.createNotification(
                    loginUserId,
                    NotificationType.AUCTION_WIN,
                    Map.of("itemName", title),
                    goodsId
            );

            // 5-3. 판매자에게 입금
            updateBalanceAndHistory(
                    sellerId,
                    goodsId,
                    bidAmount,
                    TransactionType.INCOME,      // balance + bidAmount
                    TransactionType.INCOME,
                    "입금"
            );

            // ★ 판매자(즉시구매가 낙찰) 낙찰 알림 ★
            notificationService.createNotification(
                    sellerId,
                    NotificationType.AUCTION_WIN,
                    Map.of("itemName", title),
                    goodsId
            );

            // 즉시구매면 여기서 끝 (Lock 처리 안 함)
            return;
        }

        /*
        * 6. 일반 입찰일 때만 : 새 입찰자의 예치금 Lock + BIDLOCK 기록
        * */

        updateBalanceAndHistory(
                loginUserId,
                goodsId,
                bidAmount,
                TransactionType.BIDLOCK,        // 예치금 Lock
                TransactionType.BIDLOCK,
                "입찰"
        );

        // ★ 구매자(새로운 입찰자) 입찰 성공 알림 ★
        notificationService.createNotification(
                loginUserId,
                NotificationType.AUCTION_BID_SUCCESS_BUYER,
                Map.of("itemName", title),
                goodsId
        );

        // ★ 판매자(새로운 입찰자발생) 입찰 성공 알림 ★
        notificationService.createNotification(
                sellerId,
                NotificationType.AUCTION_BID_SUCCESS_SELLER,
                Map.of("itemName", title),
                goodsId
        );

    }

    @Override
    @Transactional
    public void updateStopAuctionStatus(Long loginUserId, Long goodsId) {

        // 굿즈 + 입찰 정보 조회
        BidInternalDto.GoodsPriceBidInfo info = bidMapper.selectGoodsPriceAndBidInfoByGoodsId(goodsId);
        if(info == null) throw new NoSuchElementException("존재하지 않는 굿즈 또는 이미 종료된 경매입니다.");

        if(!Objects.equals(loginUserId, info.getSellerId())) throw new CustomException("거래중지 권한이 없습니다.", HttpStatus.FORBIDDEN);

        // 기존 최고 입찰자 예치금 unlock + 지갑 내역 BIDUNLOCK 기록
        unlockBidAmount(info.getBidderId(), goodsId, info.getCurrentBidAmount());

        // 경매 상태 -> '패찰'로 업데이트
        updateAuctionStatusOrThrow(goodsId, AuctionStatus.STOPPED);

    }



    /**
     * 경매 종료 스케줄러
     */
    @Scheduled(fixedDelay = 100000L)
    @Transactional
    public void proceessEndedAuctions() {
        log.info("-------------------------------------------------1분 경매 종료 체크 스케줄러 시작-------------------------------------------------");

        List<Goods> endedAuctions = bidMapper.selectEndedAuctions();

        if(endedAuctions == null || endedAuctions.isEmpty()) return;

        for(Goods goods : endedAuctions) {
            Long goodsId = goods.getId();
            Long sellerId = goods.getSellerId();

            // 1. 최고 입찰 조회
            BidInternalDto.GoodsPriceBidInfo highestBid =
                    bidMapper.selectGoodsPriceAndBidInfoByGoodsId(goodsId);

            if(highestBid.getBidId() == null) {
                // 입찰 없으면 패찰 처리
                updateAuctionStatusOrThrow(goodsId, AuctionStatus.STOPPED);

                continue;
            }


            Long highestBidderId = highestBid.getBidderId();
            Integer highestBidAmount = highestBid.getCurrentBidAmount();
            String title = highestBid.getTitle();

            // 2. 최고 입찰자 Lock → 실제 결제  + 지갑에 '낙찰' 기록
            updateBalanceAndHistory(
                    highestBidderId,
                    goodsId,
                    highestBidAmount,
                    TransactionType.SETTLE,    // (예치금 → 실제 지불)
                    TransactionType.EXPENSE,   // 출금
                    "경매 종료 자동 낙찰"
                    );

            // ★ 구매자(최고 입찰자) 낙찰 알림 ★
            notificationService.createNotification(
                    highestBidderId,
                    NotificationType.AUCTION_WIN,
                    Map.of("itemName", title),
                    goodsId
            );

            // 3. 판매자에게 입금 + 지갑에 '입금' 기록
            updateBalanceAndHistory(
                    sellerId,
                    goodsId,
                    highestBidAmount,
                    TransactionType.INCOME,
                    TransactionType.INCOME,
                    "경매 종료 자동 입금"
            );

            // ★ 판매자(즉시구매가 낙찰) 낙찰 알림 ★
            notificationService.createNotification(
                    sellerId,
                    NotificationType.AUCTION_WIN,
                    Map.of("itemName", title),
                    goodsId
            );


            // 5. 경매상태 '낙찰' 로 변경
            updateAuctionStatusOrThrow(goodsId, AuctionStatus.COMPLETED);


        }

        log.info("-------------------------------------------------1분 경매 종료 체크 스케줄러 종료-------------------------------------------------");
    }

    /**
     * 지갑 잔액 검증
     * @param userId 사용자Id(pk)
     * @param bidAmount 입찰 금액
     */
    private void validateWalletBalance(Long userId, int bidAmount) {
        Integer walletBalance = bidMapper.selectCoinWalletBalanceForUpdate(userId);

        if (walletBalance == null) {
            throw new CustomException("가상지갑 정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        if (walletBalance < bidAmount) {
            throw new CustomException("입찰 금액만큼의 잔액이 부족합니다.", HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 경매 상태 변경 공통 처리
     * @param goodsId 굿즈ID(pk)
     * @param auctionStatus 경매상태(enum 타입) 
     */
    private void updateAuctionStatusOrThrow(Long goodsId, AuctionStatus auctionStatus) {
        int result = bidMapper.updateAuctionStatus(goodsId, auctionStatus);
        if (result < 1) {
            throw new CustomException(
                    "경매 상태 업데이트에 실패하였습니다.",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    /**
     * 기존 최고 입찰자의 예치금 Unlock + BIDUNLOCK 거래내역 기록
     * @param userId 기존 최고 입찰자ID(pk)
     * @param goodsId 굿즈ID(pk)
     * @param amount 최고 입찰 금액
     */
    private void unlockBidAmount(Long userId, Long goodsId, Integer amount) {
        if (userId == null || amount == null || amount <= 0) {
            return;
        }

        updateBalanceAndHistory(
                userId,
                goodsId,
                amount,
                TransactionType.BIDUNLOCK,
                TransactionType.BIDUNLOCK,
                "환불"
        );
    }

    /**
     * 공통 balance 업데이트 + wallet_history 기록
     * @param userId 입찰한 사용자Id(pk)
     * @param goodsId 굿즈Id(pk)
     * @param amount 입찰 금액
     * @param balanceStatus coin_wallet balance/locked_balance 를 어떻게 바꿀지
     * @param historyType wallet_history.transaction_type
     * @param description 거래 내역
     */
    private void updateBalanceAndHistory(Long userId,
                                         Long goodsId,
                                         Integer amount,
                                         TransactionType balanceStatus,
                                         TransactionType historyType,
                                         String description) {

        BidInternalDto.CoinWalletBalance coinWalletBalance =
                BidInternalDto.CoinWalletBalance.builder()
                        .userId(userId)
                        .bidAmount(amount)
                        .balanceStatus(balanceStatus)
                        .build();

        int balanceResult = bidMapper.updateBalanceStatus(coinWalletBalance);
        if (balanceResult < 1) {
            throw new CustomException(
                    "가상화폐지갑 잔액/예치금 업데이트에 실패하였습니다.",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }

        BidInternalDto.BidWalletHistory bidWalletHistory =
                BidInternalDto.BidWalletHistory.builder()
                        .userId(userId)
                        .goodsId(goodsId)
                        .transactionType(historyType)
                        .amount(amount)
                        .description(description)
                        .build();

        int historyResult = bidMapper.insertBidWalletHistory(bidWalletHistory);
        if (historyResult < 1) {
            throw new CustomException(
                    "지갑 거래 내역 기록에 실패하였습니다.",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }
}
