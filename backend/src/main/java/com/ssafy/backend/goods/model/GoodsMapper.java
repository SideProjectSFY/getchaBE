package com.ssafy.backend.goods.model;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Mapper
public interface GoodsMapper {

    /**
     * 굿즈 등록
     * @param goods Goods
     * */
    int insertGoods(Goods goods);

    /**
     * 굿즈 다중 이미지(=파일) 업로드
     * */
    int insertFiles(List<MultipartFile> files);

    /**
     * 굿즈 목록 조회 (검색/필터)
     * */
    List<Goods> selectAllGoodsBySearch();

    /**
     * 굿즈 상세 조회
     * @param goodsId goodsId
     * */
    Goods selectGoodsById(Long goodsId);

    /**
     * 특정 굿즈 글의 다중 이미지 리스트 조회
     * @param goodsId goodsId
     * */
    GoodsImage selectImagesByGoodsId(Long goodsId);

    /**
     * 굿즈 글 수정
     * @param goods Goods
     * */
    int updateGoods(Goods goods);

    /**
     * 굿즈 글 삭제 (*경매 대기/종료 일 경우)
     * @param goodsId goodsId
     * */
    int deleteGoods(Long goodsId);

    /**
     * 굿즈 경매 상태 업데이트
     * @param goodsId goodsId
     * */
    int updateAuctionStatus(Long goodsId);


}
