package com.ssafy.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    AUCTION_WIN("축하드립니다! {itemName} 경매에 낙찰되었습니다!", "/goods?goodsId={goodsId}"),
    AUCTION_OUTBID("다른 사용자가 {itemName}에 더 높은 금액을 입찰했습니다.", "/goods?goodsId={goodsId}"),
    AUCTION_BID_SUCCESS("{userName}님! {itemName} 입찰이 성공적으로 등록되었습니다.", "/goods?goodsId={goodsId}"),
    AUCTION_ENDING_SOON("{itemName} 경매 종료 5분 전입니다! 서둘러 확인해보세요.", "/goods?goodsId={goodsId}"),
    AUCTION_BUY_NOW("{itemName}을 즉시 구매하였습니다!", "/goods?goodsId={goodsId}"),
    AUCTION_CLOSED("{itemName} 경매가 종료 되었습니다!", "/goods?goodsId={goodsId}"),
    AUCTION_STARTED("{itemName} 경매가 시작 되었습니다!", "/goods?goodsId={goodsId}");

    private final String message;
    private final String linkTemplate;
}
