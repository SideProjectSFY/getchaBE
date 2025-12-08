package com.ssafy.backend.goods.model;

import com.ssafy.backend.common.enums.AuctionStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
     * @param goodsId 조회할 굿즈ID(pk)
     * @param loginUserId 로그인한 사용자ID(pk)
     * @return 굿즈ID 로 조회한 굿즈 정보
     */
    GoodsResponseDto.GoodsDetail selectGoodsById(Long goodsId, Long loginUserId);


    /**
     * 경매에 입찰한 참여자 리스트 조회
     * @param goodsId 굿즈Id(pk)
     * @return 경매 입찰자 리스트 결과 반환
     */
    List<GoodsResponseDto.BidParticipant> selectBidParticipantByGoodsId(Long goodsId);

    /**
     * 특정 굿즈 글의 다중 이미지 리스트 조회
     * @param goodsId 굿즈ID(pk)
     * @return 굿즈ID 로 조회한 굿즈 다중 이미지
     * */
    List<GoodsResponseDto.GoodsDetailImage> selectImagesByGoodsId(Long goodsId);

    /**
     * 기존 이미지 순서 업데이트
     * @param images 기존이미지 정보
     * @return 결과 반환
     */
    int updateImageSortOrder(@Param("goodsId") Long goodsId,
                            @Param("images") List<GoodsRequestDto.GoodsExistingImages> images);


    /**
     * 신규 이미지의 다음 순서 조회
     * @param goodsId 굿즈Id(pk)
     * @return 다음 순서 결과 반환
     */
    Integer selectNextSortOrder(Long goodsId);

    /**
     * 삭제할 이미지파일ID 리스트 의 정보 조회
     * @param deleteImageIds 삭제할 이미지파일ID 리스트
     * @return 이미지 리스트 결과 반환
     */
    List<GoodsImage> selectDeleteImageFileByFileId(List<Long> deleteImageIds);

    /**
     * 이미지 삭제
     * @param deleteImageIds 삭제할 이미지파일ID 리스트
     * @return 삭제 결과 반환 
     */
    int deleteGoodsImageByFileId(List<Long> deleteImageIds);

    /**
     * 굿즈 글 정보 수정
     * @param goodsModify 수정할 굿즈 정보
     * @return 굿즈 글 수정 결과 반환
     * */
    int updateGoods(@Param("goodsModify") GoodsRequestDto.GoodsModify goodsModify,
                    @Param("loginUserId") Long loginUserId,
                    @Param("auctionEndAt") LocalDateTime auctionEndAt);

    /**
     * 굿즈 글 삭제 (*경매 대기/종료 일 경우)
     * @param goodsId 삭제할 굿즈ID
     * @param loginUserId 로그인한 사용자ID(pk)
     * @return 굿즈 글 삭제 결과 반환
     */
    int deleteGoods(Long goodsId, Long loginUserId);

    /**
     * 특정 굿즈 경매 상태 조회
     * @param goodsId 경매상태 조회할 굿즈ID
     * @return 경매 상태
     */
    AuctionStatus selectAuctionStatusByGoodsId(Long goodsId);

}
