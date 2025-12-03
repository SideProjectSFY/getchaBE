package com.ssafy.backend.bid.service.impl;

import com.ssafy.backend.bid.model.*;
import com.ssafy.backend.bid.service.BidService;
import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.goods.model.GoodsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Slf4j
@Service
public class BidServiceImpl implements BidService {

    private final BidMapper bidMapper;
    private final GoodsMapper goodsMapper;

    @Override
    @Transactional
    public void postBidForGoods(BidRequestDto.BidRegister bidRegister) {
        // TODO : 토큰 정보에서 로그인 ID 뽑아오기
        Long loginUserId = 10L;
        /*
        * <조건>
        * 1. 굿즈를 등록한 판매자는 입찰할 수 없음
        * 2. 본인이 현재 최고 입찰자인 상태일 땐, 다시 재입찰 못함
        * 3. 현재 최고 입찰가보다 높으면서, 즉시 구매가 이하인 금액만 입찰 가능
        * */
        Long goodsId = bidRegister.getGoodsId();
        int bidAmount = bidRegister.getBidAmount();

        // 입찰로 등록할 bid 객체 빌드
        Bid bid = Bid.builder()
                .goods_id(goodsId)
                .bidder_id(loginUserId)
                .bid_amount(bidAmount)
                .build();


        BidInternalDto.GoodsPriceBidInfo goodsPriceBidInfo = bidMapper.selectGoodsPriceAndBidInfoByGoodsId(bidRegister.getGoodsId());
        if(goodsPriceBidInfo == null) throw new NoSuchElementException("존재하지 않는 굿즈 글입니다");

        Long bidId = goodsPriceBidInfo.getBidId();
        int instantBuyPrice = goodsPriceBidInfo.getInstantBuyPrice();
        /* TODO : 입찰금액이 즉시 구매가와 같거나 크다면 바로 낙찰 하는 기능 만들기
        * 바로 해당 굿즈 상태를 '낙찰' 로 변경
        *  - 입찰 금액이 없다면
        * 1. 첫 입찰자 즉시 입찰 등록
        * 2. 그 후 바로 5번 부터 진행
        *  - 입찰 금액이 있다면 5번까지 진행 후
        * 1. 새 입찰자의 입찰금을 lock 거는 것이 아닌 바로 coin_wallet 의 balence 에서 최고 입찰가 돈을 뺀다.
        * 2. wallet_history 에 (출금) 정산 완료 로 내역 기록
        * */

        // 1-1. 만약 입찰 금액이 없다면, 굿즈 등록 금액과 비교해야함
        if(bidId == null) {
            if(bidAmount >= goodsPriceBidInfo.getStartPrice()) {
                //  => 해당 굿즈 상태가 대기이므로, 상태를 '경매 진행'으로 변경
                int updateAuctionStatusResult = goodsMapper.updateAuctionStatus(bidRegister.getGoodsId(), AuctionStatus.PROCEEDING);
                if(updateAuctionStatusResult < 1)
                    throw new CustomException("경매 상태 업데이트에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);

                //  => 바로 5번부터 순차적으로 입찰 등록 아래에서 처리
            } else {
                throw new CustomException("시작가 이상부터 입찰이 가능합니다.", HttpStatus.BAD_REQUEST);
            }
        } else {
            // 1-2. 현재 최고 입찰가보다 높은 금액인지 비교
            int currentBidAmount = goodsPriceBidInfo.getCurrentBidAmount();
            if(bidAmount < currentBidAmount) {
                throw new CustomException("현재 입찰가 이상부터 입찰이 가능합니다.", HttpStatus.BAD_REQUEST);
            }

            // 2. 현재 최고가 금액 표시 상태를 기본 입찰 금액 상태로 update
            int updateIsHighestResult = bidMapper.updateIsHighestByGoodsId(goodsId);
            if(updateIsHighestResult < 1) throw new CustomException("현재 최고가 여부 업데이트에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

            // 3. 기존 최고 입찰자 예치금 unlock


            // 4. 기존 최고 입찰자의 지갑 내역에 굿즈 글 제목 + 환불 기록해주기

        }
        // 5. 새로운 입찰자가 제시한 금액을 현재 최고가로 입찰 등록
        int insertCurrentBidAmountResult = bidMapper.insertCurrentBidAmount(bid);
        if(insertCurrentBidAmountResult < 1)
            throw new CustomException("입찰금액 등록에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);

        // 6. 새 입찰자의 예치금 Lock 처리

        // 7. 새 입찰자의 지갑 내역에 기록해주기

    }

    @Override
    public List<BidResponseDto.BidParticipant> getAllParticipant(Long goodsId) {

        List<BidResponseDto.BidParticipant> bidParticipantList = bidMapper.selectBidParticipantByGoodsId(goodsId);
        if(ObjectUtils.isEmpty(bidParticipantList)) throw new NoSuchElementException("경매에 참여한 사람이 없습니다.");

        return bidParticipantList;
    }
}
