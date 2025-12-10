package com.ssafy.backend.goods.service;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.goods.model.GoodsRequestDto;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface GoodsService {

    /**
     * 굿즈 등록
     *  @param loginUserId 로그인한 사용자Id(pk)
     *  @param goodsRegister 등록된 굿즈 정보
     *  @param imageFiles 등록된 굿즈 이미지 파일리스트
     *  @return 굿즈 등록 ID(pk)
     * */
    GoodsResponseDto.AddGoodsResult addGoods(
            Long loginUserId,
            GoodsRequestDto.GoodsRegister goodsRegister,
            List<MultipartFile> imageFiles
    );


    /**
     * 굿즈 카드 목록 조회 (검색/필터)
     * @return 굿즈 카드 목록 리스트
     */
    PageResponse<GoodsResponseDto.GoodsCard> getAllGoods(GoodsRequestDto.GoodsLookUp goodsLookUp);

    /**
     * 굿즈 상세 조회
     * @param loginUserId 로그인한 사용자Id(pk)
     * @param goodsId 굿즈ID(pk)
     * @return 조회된 굿즈 정보, 없으면 null
     */
    GoodsResponseDto.GoodsDetailAll getGoodsById(Long loginUserId, Long goodsId);

    /**
     * 굿즈 글 정보 수정
     * @param loginUserId 로그인한 사용자Id(pk)
     * @param goodsModify 수정된 굿즈 정보
     * @param newImageFiles   새로 추가된 굿즈 이미지 파일리스트
     */
    void updateGoods(
            Long loginUserId,
            GoodsRequestDto.GoodsModify goodsModify,
            List<MultipartFile> newImageFiles
    );

    /**
     * 굿즈 삭제
     * @param goodsId 삭제할 굿즈 ID
     */
    void deleteGoods(Long loginUserId, Long goodsId);

}
