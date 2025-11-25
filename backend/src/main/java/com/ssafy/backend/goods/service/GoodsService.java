package com.ssafy.backend.goods.service;

import com.ssafy.backend.goods.model.GoodsRequestDto;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface GoodsService {

    /**
     * 굿즈 등록
     *  @param goodsRegister 등록된 굿즈 정도
     *  @param files 등록된 굿즈 이미지 파일리스트
     *  @return 등록 성공 여부
     * */
    boolean addGoods(GoodsRequestDto.GoodsRegister goodsRegister, MultipartFile[] files);

    /**
     * 굿즈 카드 목록 조회
     * @return 굿즈 카드 목록 리스트
     */
    List<GoodsResponseDto.GoodsCardVO> getAllGoods();

    /**
     * 굿즈 상세 조회
     * @param goodsId goodsId
     * @return 조회된 굿즈 정보, 없으면 null
     */
    GoodsResponseDto.GoodsDetailAllVO getGoodsById(Long goodsId);

    /**
     * 굿즈 정보 수정
     *
     * @param goodsModify 수정된 굿즈 정보
     * @param files   수정된 굿즈 이미지 파일리스트
     * @return 수정 성공 여부
     */
    boolean updateGoods(GoodsRequestDto.GoodsModify goodsModify, MultipartFile[] files);

    /**
     * 굿즈 삭제
     *
     * @param goodsId 삭제할 굿즈 ID
     * @return 삭제 성공 여부
     */
    boolean deleteGoods(Long goodsId);

}
