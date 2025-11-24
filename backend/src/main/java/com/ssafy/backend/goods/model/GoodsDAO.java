package com.ssafy.backend.goods.model;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Mapper
public interface GoodsDAO {

    // 굿즈 등록
    int insertGoods(Goods goods);

    // 굿즈 다중 이미지(=파일) 업로드
    int insertFiles(List<MultipartFile> files);

    // 굿즈 목록 조회 (검색/필터)
    List<Goods> selectAllGoodsBySearch();

    // 굿즈 상세 조회
    Goods selectGoodsById(int id);

    // 굿즈 글 수정
    int updateGoods(Goods goods);

    // 굿즈 글 삭제 (*경매 대기/종료 일 경우)
    int deleteGoods(int id);

    // 굿즈 경매 상태 업데이트
    int updateAuctionStatus(int id);


}
