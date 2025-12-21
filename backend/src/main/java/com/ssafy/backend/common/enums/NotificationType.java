package com.ssafy.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    AUCTION_WIN_BUYER("축하합니다! '{itemName}'에 낙찰되었습니다.", "/goods?goodsId={goodsId}"),
    AUCTION_WIN_SELLER("축하합니다! '{itemName}'가 판매되었습니다.", "/goods?goodsId={goodsId}"),
    AUCTION_OUTBID("다른 사용자가 '{itemName}'에 더 높은 금액을 입찰하였습니다. 다시 도전해보세요!", "/goods?goodsId={goodsId}"),
    AUCTION_BID_SUCCESS_BUYER("'{itemName}' 입찰에 성공하였습니다.", "/goods?goodsId={goodsId}"),
    AUCTION_BID_SUCCESS_SELLER("새로운 입찰자가 '{itemName}'에 입찰하였습니다. 확인해보세요!", "/goods?goodsId={goodsId}"),
    AUCTION_ENDING_SOON("'{itemName}' 경매 종료 5분 전입니다! 서둘러 확인해보세요.", "/goods?goodsId={goodsId}"),
    AUCTION_BUY_NOW("'{itemName}'이 즉시 구매되었습니다.", "/goods?goodsId={goodsId}"),
    AUCTION_CLOSED("'{itemName}' 경매가 종료 되었습니다.", "/goods?goodsId={goodsId}"),
    AUCTION_STARTED("'{itemName}' 경매가 시작 되었습니다.", "/goods?goodsId={goodsId}");

    private final String message;
    private final String linkTemplate;
}
