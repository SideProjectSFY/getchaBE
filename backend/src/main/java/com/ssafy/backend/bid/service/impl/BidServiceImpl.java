package com.ssafy.backend.bid.service.impl;

import com.ssafy.backend.bid.model.*;
import com.ssafy.backend.bid.service.BidService;
import com.ssafy.backend.comment.model.CommentMapper;
import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.enums.TransactionType;
import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.goods.model.Goods;
import com.ssafy.backend.goods.model.GoodsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class BidServiceImpl implements BidService {

    private final BidMapper bidMapper;
    private final GoodsMapper goodsMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public void postBidForGoods(BidRequestDto.BidRegister bidRegister) {
        // TODO : 토큰 정보에서 로그인 ID 뽑아오기
        Long loginUserId = 3L;
        final int limitAmount = 5_000_000;

        // 가상화페DTO, 거래내역DTO 선언
        BidInternalDto.CoinWalletBalance coinWalletBalance;
        BidInternalDto.WalletHistoryAndUserId walletHistory;
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
        int bidAmount = bidRegister.getBidAmount();

        // 굿즈 + 입찰 정보 조회
        BidInternalDto.GoodsPriceBidInfo info = bidMapper.selectGoodsPriceAndBidInfoByGoodsId(bidRegister.getGoodsId());
        if(info == null) throw new NoSuchElementException("존재하지 않는 굿즈 글입니다");

        Long sellerId = info.getSellerId();
        Long bidId = info.getBidId();                           // null 이면 첫 입찰
        Integer startPrice = info.getStartPrice();
        Integer currentBidAmount = info.getCurrentBidAmount();  // null 이면 첫 입찰
        Integer instantBuyPrice = info.getInstantBuyPrice();
        Long beforeBidderId = info.getBidderId();               // 기존 최고 입찰자

        boolean isFirstBid = (bidId == null);
        boolean isInstantBuy = (bidAmount >= instantBuyPrice);

        /*
        * 0. 공통 기본 검증
        * */

        // 최고 입찰자는 재입찰 불가
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
                if(bidAmount < startPrice || bidAmount > limitAmount)
                    throw new CustomException("시작가 이상, 제한금액(500만원) 이하일 때 입찰이 가능합니다.", HttpStatus.BAD_REQUEST);

                // 1-2. 첫 입찰 성공 -> 상태를 '경매 진행'으로 변경(WAIT -> PROCEEDING)
                int updateStatusResult = goodsMapper.updateAuctionStatus(bidRegister.getGoodsId(), AuctionStatus.PROCEEDING);
                if(updateStatusResult < 1)
                    throw new CustomException("경매 상태 업데이트에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);

                //  이후 흐름은 5번(입찰등록)부터 공통 처리
            }
            // 첫입찰 + 즉시 구매가 이상인 경우 위 로직 안돌리고 바로 5번(입찰등록)부터 공통처리
        } else {
            // 기존 입찰이 있는 경우

            // 1-3 현재 최고 입찰가 이상, 제한금액 이하인지 체크
            if(bidAmount < currentBidAmount || bidAmount > limitAmount) {
                throw new CustomException("현재 입찰가 이상, 제한금액(500만원) 이하일 때 입찰이 가능합니다.", HttpStatus.BAD_REQUEST);
            }

            /*
            * 2. 기존 최고 입찰 isHighest 여부 false 로 업데이트
            * */

            int updateIsHighestResult = bidMapper.updateIsHighestByGoodsId(goodsId);
            if(updateIsHighestResult < 1) throw new CustomException("현재 최고가 여부 업데이트에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            /*
            * 3. 기존 최고 입찰자 예치금 unlock
            * */
            coinWalletBalance = BidInternalDto.CoinWalletBalance.builder()
                    .bidAmount(currentBidAmount)
                    .userId(beforeBidderId)
                    .balanceStatus(TransactionType.BIDUNLOCK)
                    .build();

            int unlockResult = bidMapper.updateBalanceStatus(coinWalletBalance);
            if(unlockResult < 1) throw new CustomException("이전 입찰자의 예치금 환원을 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            /*
            * 4. 기존 최고 입찰자의 지갑 내역 BIDUNLOCK 기록
            * */
            walletHistory = BidInternalDto.WalletHistoryAndUserId.builder()
                    .userId(beforeBidderId)
                    .goodsId(goodsId)
                    .transactionType(TransactionType.BIDUNLOCK)
                    .amount(currentBidAmount)
                    .description("환불")
                    .build();

            int insertHistoryResult = bidMapper.insertWalletHistory(walletHistory);
            if(insertHistoryResult < 1) throw new CustomException("이전 입찰자의 거래 내역 기록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // 0-3. 새 입찰자의 코인 잔액 검증 (LOCK 또는 PAY 가능 여부 확인)
        Integer walletBalance = bidMapper.selectCoinWalletBalanceForUpdate(loginUserId);
        if (walletBalance == null)
            throw new CustomException("가상지갑 정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

        if (walletBalance < bidAmount)
            throw new CustomException("입찰 금액만큼의 잔액이 부족합니다.", HttpStatus.BAD_REQUEST);



        /*
        * 5. 새로운 입찰 등록
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
        * 6. 즉시구매 분기
        * */
        if(isInstantBuy) {
            // 6-1. 굿즈 상태를 '낙찰' 로 변경
            int updateStatusResult = goodsMapper.updateAuctionStatus(goodsId, AuctionStatus.COMPLETED);
            if(updateStatusResult < 1)
                throw new CustomException("경매 상태 업데이트에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);

            // 6-2. 새 입찰자의 금액을 Lock 이 아닌 실제 출금 처리
            coinWalletBalance = BidInternalDto.CoinWalletBalance.builder()
                    .bidAmount(bidAmount)
                    .userId(loginUserId)
                    .balanceStatus(TransactionType.EXPENSE) // 정산
                    .build();

            int payResult = bidMapper.updateBalanceStatus(coinWalletBalance);
            if (payResult < 1) throw new CustomException("낙찰 금액 정산에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            // 6-3. wallet_history 에 (출금) 정산 완료 기록
            walletHistory = BidInternalDto.WalletHistoryAndUserId.builder()
                    .userId(loginUserId)
                    .goodsId(goodsId)
                    .transactionType(TransactionType.EXPENSE)
                    .amount(bidAmount)
                    .description("낙찰")
                    .build();

            int payHistoryResult = bidMapper.insertWalletHistory(walletHistory);
            if (payHistoryResult < 1)
                throw new CustomException("낙찰 거래 내역 기록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            // 6-4. 판매자에게 입금
            coinWalletBalance = BidInternalDto.CoinWalletBalance.builder()
                    .bidAmount(bidAmount)
                    .userId(sellerId)
                    .balanceStatus(TransactionType.INCOME) // 입금
                    .build();

            int incomeResult = bidMapper.updateBalanceStatus(coinWalletBalance);
            if (incomeResult < 1) throw new CustomException("입금 금액 정산에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            // 6-5. wallet_history 에 (입금) 정산 완료 기록
            walletHistory = BidInternalDto.WalletHistoryAndUserId.builder()
                    .userId(sellerId)
                    .goodsId(goodsId)
                    .transactionType(TransactionType.INCOME)
                    .amount(bidAmount)
                    .description("입금")
                    .build();

            int incomeHistoryResult = bidMapper.insertWalletHistory(walletHistory);
            if (incomeHistoryResult < 1)
                throw new CustomException("입금 거래 내역 기록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            // 즉시구매면 여기서 끝 (Lock 처리 안 함)
            return;
        }

        /*
        * 7. 일반 입찰일 때만 : 새 입찰자의 예치금 Lock + BIDLOCK 기록
        * */
        coinWalletBalance = BidInternalDto.CoinWalletBalance.builder()
                .bidAmount(bidAmount)
                .userId(loginUserId)
                .balanceStatus(TransactionType.BIDLOCK)
                .build();


        int lockResult = bidMapper.updateBalanceStatus(coinWalletBalance);
        if(lockResult < 1) throw new CustomException("새로운 입찰자의 예치금 잠금을 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

        walletHistory = BidInternalDto.WalletHistoryAndUserId.builder()
                .userId(loginUserId)
                .goodsId(goodsId)
                .transactionType(TransactionType.BIDLOCK)
                .amount(bidAmount)
                .description("입찰")
                .build();

        int lockHistoryResult = bidMapper.insertWalletHistory(walletHistory);
        if(lockHistoryResult < 1) throw new CustomException("새로운 입찰자의 입찰 거래 내역 기록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

    }

    @Override
    public List<BidResponseDto.BidParticipant> getAllParticipant(Long goodsId) {

        if (commentMapper.checkGoodsId(goodsId) == 0) {
            throw new NoSuchElementException("존재하지 않는 굿즈입니다.");
        }

        List<BidResponseDto.BidParticipant> participants = bidMapper.selectBidParticipantByGoodsId(goodsId);
        // 참여자가 아예 없는 경우가 있기 때문에 에러로 반환해선 안된다.
        if (participants == null || participants.isEmpty()) {
            return Collections.emptyList();
        }

        return participants;
    }

//    @Scheduled(fix)
    @Transactional
    public void proceessEndedAuctions() {
        List<Goods> endedAuctions = bidMapper.selectEndedAuctions();

        if(endedAuctions.isEmpty()) return;

        for(Goods goods : endedAuctions) {
            Long goodsId = goods.getId();
            Long sellerId = goods.getSellerId();

            // 1. 최고 입찰 조회
            BidInternalDto.GoodsPriceBidInfo highestBid = bidMapper.selectGoodsPriceAndBidInfoByGoodsId(goodsId);

            if(highestBid.getBidId() == null) {
                // 입찰 없으면 패찰 처리
                int updateStatusResult = goodsMapper.updateAuctionStatus(goodsId, AuctionStatus.STOPPED);
                if(updateStatusResult < 1)
                    throw new CustomException("경매 상태 업데이트에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);
                continue;
            }

            Long highestBidderId = highestBid.getBidderId();
            Integer highestBidAmount = highestBid.getCurrentBidAmount();

            // 2. 최고 입찰자 Lock → Pay
            BidInternalDto.CoinWalletBalance coinWalletBalance = BidInternalDto.CoinWalletBalance.builder()
                    .bidAmount(highestBidAmount)
                    .userId(highestBidderId)
                    .balanceStatus(TransactionType.SETTLE) // 예치금Lock 된 금액을 실제 지불 
                    .build();

            int settleResult = bidMapper.updateBalanceStatus(coinWalletBalance);
            if (settleResult < 1) throw new CustomException("낙찰 금액 정산에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            // 3. wallet_history 에 (출금) 정산 완료 기록
            BidInternalDto.WalletHistoryAndUserId walletHistory = BidInternalDto.WalletHistoryAndUserId.builder()
                    .userId(highestBidderId)
                    .goodsId(goodsId)
                    .transactionType(TransactionType.EXPENSE)
                    .amount(highestBidAmount)
                    .description("낙찰")
                    .build();

            int settleHistoryResult = bidMapper.insertWalletHistory(walletHistory);
            if (settleHistoryResult < 1)
                throw new CustomException("낙찰 거래 내역 기록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            // 4. 판매자에게 입금
            coinWalletBalance = BidInternalDto.CoinWalletBalance.builder()
                    .bidAmount(highestBidAmount)
                    .userId(sellerId)
                    .balanceStatus(TransactionType.INCOME) // 입금
                    .build();

            int incomeResult = bidMapper.updateBalanceStatus(coinWalletBalance);
            if (incomeResult < 1) throw new CustomException("입금 금액 정산에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            // 5. wallet_history 에 (입금) 정산 완료 기록
            walletHistory = BidInternalDto.WalletHistoryAndUserId.builder()
                    .userId(sellerId)
                    .goodsId(goodsId)
                    .transactionType(TransactionType.INCOME)
                    .amount(highestBidAmount)
                    .description("입금")
                    .build();

            int incomeHistoryResult = bidMapper.insertWalletHistory(walletHistory);
            if (incomeHistoryResult < 1)
                throw new CustomException("입금 거래 내역 기록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);
            
            // 6. 경매상태 '낙찰' 로 변경 
            int updateStatusResult = goodsMapper.updateAuctionStatus(goodsId, AuctionStatus.COMPLETED);
            if(updateStatusResult < 1)
                throw new CustomException("경매 상태 업데이트에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);
            
        }
    }
}
