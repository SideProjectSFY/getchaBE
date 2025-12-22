package com.ssafy.backend.ai.model;

import com.ssafy.backend.goods.model.Goods;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendedGoodsDto {
    private Goods goods;
    private int matchRate;
    private boolean aiBased; //AI 추천 애니 리스트에 있는 녀석인지
}
