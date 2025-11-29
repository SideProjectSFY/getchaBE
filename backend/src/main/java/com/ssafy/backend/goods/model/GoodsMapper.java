package com.ssafy.backend.goods.model;

import com.ssafy.backend.common.enums.AuctionStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Mapper
public interface GoodsMapper {

    /**
     * 굿즈 등록
     * @param goods 굿즈 정보
     * @return 굿즈 등록 결과반환
     * */
    int insertGoods(Goods goods);


    /**
     * 굿즈 이미지(=파일) 업로드
     * @param imageFile 이미지파일
     */
    void insertFiles(GoodsImage imageFile);

    /**
     * 굿즈 다중 이미지(=파일) 수정
     * @param files 다중이미지파일
     * @return 파일들 수정 결과
     */
    int updateFiles(List<MultipartFile> files);

    /**
     * 굿즈 카드 목록 조회 (검색/필터)
     * @param goodsLookUp 굿즈 조회용 정보
     * @return 조회된 굿즈카드 리스트
     */
    List<GoodsResponseDto.GoodsCard> selectAllGoodsBySearch(GoodsRequestDto.GoodsLookUp goodsLookUp);

    /**
     * 굿즈 카드 총 개수
     * @param goodsLookUp 굿즈 조회용 정보
     * @return 굿즈 카드 총 개수
     */
    int countGoods(GoodsRequestDto.GoodsLookUp goodsLookUp);

    /**
     * 굿즈 상세 조회
     * @param goodsId 굿즈ID(pk)
     * @return 굿즈ID 로 조회한 굿즈 정보
     * */
    Goods selectGoodsById(Long goodsId);

    /**
     * 특정 굿즈 글의 다중 이미지 리스트 조회
     * @param goodsId 굿즈ID(pk)
     * @return 굿즈ID 로 조회한 굿즈 다중 이미지
     * */
    GoodsImage selectImagesByGoodsId(Long goodsId);

    /**
     * 굿즈 글 정보 수정
     * @param goods 수정할 굿즈 정보
     * @return 굿즈 글 수정 결과 반환
     * */
    int updateGoods(Goods goods);

    /**
     * 굿즈 글 삭제 (*경매 대기/종료 일 경우)
     * @param goodsId 삭제할 굿즈ID
     * @return 굿즈 글 삭제 결과 반환
     * */
    int deleteGoods(Long goodsId);

    /**
     * 특정 굿즈 경매 상태 조회
     * @param goodsId 경매상태 조회할 굿즈ID
     * @return 경매 상태
     */
    AuctionStatus selectAuctionStatusByGoodsId(Long goodsId);

    /**
     * 굿즈 경매 상태 업데이트
     * @param goodsId 굿즈ID(pk)
     * @return 굿즈 경매 상태 업데이트 결과반환
     * */
    int updateAuctionStatus(Long goodsId, AuctionStatus auctionStatus);

}
