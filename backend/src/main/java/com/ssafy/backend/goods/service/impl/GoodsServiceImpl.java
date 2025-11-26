package com.ssafy.backend.goods.service.impl;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.goods.model.Goods;
import com.ssafy.backend.goods.model.GoodsMapper;
import com.ssafy.backend.goods.model.GoodsRequestDto;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import com.ssafy.backend.goods.service.GoodsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class GoodsServiceImpl implements GoodsService {

    /**
     * TODO
     * 1. 필수 !!!
     * - 토큰 정보 파싱 후 사용자 정보 받아오기
     */

    private final GoodsMapper goodsMapper;

    @Override
    public boolean addGoods(GoodsRequestDto.GoodsRegister goodsRegister, MultipartFile[] files) {
        // 1. 카테고리 String -> 비교 후 Enum 타입으로 변경 (없으면 기타)

        // 2. 경매기간 받아서, 종료일시 저장
        // 시간 맞춰야해서 따로 저장
        LocalDateTime createdAt = LocalDateTime.now();
        int duration = goodsRegister.getDuration();
        LocalDateTime auctionEndAt = createdAt.plusDays(duration);

//        Goods goods = Goods.builder()
//                .sellerId() // TODO : Token 파싱 후 넣을 userId
//                .
//                .build();


        // 3. 굿즈 등록

        // 4. 이미지 업로드
        return false;
    }

    @Override
    public PageResponse<GoodsResponseDto.GoodsCard> getAllGoods(GoodsRequestDto.GoodsLookUp goodsLookUp) {

        // 목록 반환 결과 받은 뒤 남은 기간 계산해서 setting

        return null;
    }


    @Override
    public GoodsResponseDto.GoodsDetailAll getGoodsById(Long goodsId) {

        // 굿즈 데이터

        // 댓글 데이터

        // 입찰 관련 데이터 (+경매 참여자)

        // 모든 데이터 합쳐서 GoodsDetailAll 에 넣어서 반환

        return null;
    }

    @Override
    public boolean updateGoods(GoodsRequestDto.GoodsModify goodsModify, MultipartFile[] files) {
        // 1. duration 이 update 되었다면 auction_end_at 도 계산해서 update
        return false;
    }

    @Override
    public boolean deleteGoods(Long goodsId) {
        return false;
    }

    @Override
    public boolean updateAuctionStatus(Long goodsId, String auctionStatus) {
        return false;
    }
}
