package com.ssafy.backend.goods.service.impl;

import com.ssafy.backend.bid.model.BidMapper;
import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.goods.model.*;
import com.ssafy.backend.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {

    /**
     * TODO
     * 1. 필수 !!!
     * - 토큰 정보 파싱 후 사용자 정보 받아오기
     */
    private final GoodsMapper goodsMapper;

    // 실제 서버 저장 상대경로 prefix
    @Value("${file.upload.prefix}")
    private String filePrefix;

    // 실제 서버 저장 상대경로 filePath
    @Value("${file.upload.path}")
    private String filePath;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoods(GoodsRequestDto.GoodsRegister goodsRegister, List<MultipartFile> imageFiles) {
        // TODO : 로그인 되었는지 체크 !

        // 2. 경매기간 받아서, 종료일시 저장
        // 시간 맞춰야해서 따로 저장
        LocalDateTime createdAt = LocalDateTime.now();
        int duration = goodsRegister.getDuration();
        LocalDateTime auctionEndAt = createdAt.plusDays(duration);

        // 금액 검증
        Integer startPrice = goodsRegister.getStartPrice();
        Integer instantBuyPrice = goodsRegister.getInstantBuyPrice();
        if(instantBuyPrice < startPrice)
            throw new CustomException("즉시구매가는 시작가 이상이어야 합니다.", HttpStatus.BAD_REQUEST);

        Goods goods = Goods.builder()
                .sellerId(1L) // TODO : Token 파싱 후 넣을 userId
                .animeId(goodsRegister.getAnimeId())
                .category(goodsRegister.getCategory())
                .title(goodsRegister.getTitle())
                .description(goodsRegister.getDescription())
                .startPrice(startPrice)
                .instantBuyPrice(instantBuyPrice)
                .duration(duration)
                .auctionEndAt(auctionEndAt)
                .createdAt(createdAt)
                .build();

        // 3. 굿즈 등록
        int saveResult = goodsMapper.insertGoods(goods);
        // 굿즈 등록에 실패 시 에러 던지기
        if(saveResult < 1) throw new CustomException("굿즈 글 등록에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);

        Long goodsId = goods.getId();

        // 4. 이미지 업로드
        int sortOrder = 1;
        for (MultipartFile file : imageFiles) {

            if(file.isEmpty()) continue;

            if(sortOrder > 5) break;

            String originalFilename = file.getOriginalFilename();
            String storedFilename = generateStoredFilename(originalFilename);

            // 실제 파일 저장
            saveFileToLocal(file, storedFilename);

            GoodsImage imageFile = GoodsImage.builder()
                    .goodsId(goodsId)
                    .originFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .filePath(filePath + "/" + storedFilename)
                    .fileSize(file.getSize())
                    .sortOrder(sortOrder++)
                    .build();

            // DB 저장
            goodsMapper.insertFiles(imageFile);
        }

    }



    @Override
    public PageResponse<GoodsResponseDto.GoodsCard> getAllGoods(GoodsRequestDto.GoodsLookUp goodsLookUp) {

        goodsLookUp.setPageOffset(goodsLookUp.getOffset());

        List<GoodsResponseDto.GoodsCard> goodsCardsList = goodsMapper.selectAllGoodsBySearch(goodsLookUp);

        long totalCount = goodsMapper.countGoods(goodsLookUp);
        if(totalCount == 0) throw new NoSuchElementException("등록된 굿즈가 없습니다.");

        int totalPages = (int) Math.ceil((double) totalCount / goodsLookUp.getSize());

        return new PageResponse<>(goodsCardsList, goodsLookUp.getPage(), totalPages, totalCount);
    }


    @Override
    public GoodsResponseDto.GoodsDetailAll getGoodsById(Long goodsId) {
        // TODO : 본인이 쓴 글인지 확인
        Long loginUserId = 1L;

        // 굿즈 데이터 조회
        GoodsResponseDto.GoodsDetail goodsDetail = goodsMapper.selectGoodsById(goodsId, loginUserId);

        if (goodsDetail == null) {
            throw new NoSuchElementException("존재하지 않는 굿즈입니다.");
        }

        // 이미지 리스트 조회
        List<GoodsResponseDto.GoodsDetailImage> imageList = goodsMapper.selectImagesByGoodsId(goodsId);
        if(imageList == null || imageList.isEmpty()) imageList =  Collections.emptyList();

        // 입찰 관련 데이터 (+경매 참여자)
        List<GoodsResponseDto.BidParticipant> participants = goodsMapper.selectBidParticipantByGoodsId(goodsId);

        if (participants == null || participants.isEmpty()) {
            participants =  Collections.emptyList();
        }

        // 모든 데이터 합쳐서 GoodsDetailAll 에 넣어서 반환
        return GoodsResponseDto.GoodsDetailAll.builder()
                .goodDetail(goodsDetail)
                .imageList(imageList)
                .participants(participants)
                .build();
    }

    @Override
    @Transactional
    public boolean updateGoods(GoodsRequestDto.GoodsModify goodsModify, List<MultipartFile> imageFiles) {
        // TODO : 본인이 쓴 글인지 확인하는 과정 핋요
        Long loginUserId = 1L;

        AuctionStatus auctionStatus = goodsModify.getAuctionStatus();
        Long sellerId = goodsModify.getSellerId();
        LocalDateTime createdAt = goodsModify.getCreatedAt();
        int duration = goodsModify.getDuration();

        // 경매 대기 상태 검증
        if(auctionStatus != AuctionStatus.WAIT)
            throw new CustomException("경매 상태가 대기일 경우에만 수정이 가능합니다.", HttpStatus.BAD_REQUEST);

        // 본인이 쓴글인지 검증
        if(!Objects.equals(sellerId, loginUserId))
            throw new CustomException("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);

        // 경매기간(duration) 수정이 될 경우를 대비하여, auction_end_at 도 작성일시를 기준으로 수정하기
        LocalDateTime auctionEndAt = createdAt.plusDays(duration);
        goodsModify.setAuctionEndAt(auctionEndAt);

        // 굿즈 데이터 수정
        int updateGoodsResult = goodsMapper.updateGoods(goodsModify);
        if(updateGoodsResult < 1)
            throw new CustomException("굿즈 글 수정에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);


        /* TODO : 이미지 리스트 수정
        *  1. 삭제 요청된 이미지 삭제 (DB + 실제 파일 Hard 삭제)
        *  2. 유지되는 기존 이미지들의 sort_order 업데이트
        *  3. 신규 이미지 업로드 & INSERT
        * */

        // 1. 삭제 요청된 이미지 삭제 (DB + 실제 파일 Hard 삭제)
        List<Long> deleteImageIds = goodsModify.getDeleteImageIds();
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            List<GoodsImage> deleteImages = goodsMapper.selectDeleteImageFileByFileId(deleteImageIds);

            for (GoodsImage img : deleteImages) {
                File file = new File(filePath + File.separator + img.getStoredFilename());
                if (file.exists()) file.delete();
            }

            int deleteImageFile = goodsMapper.deleteGoodsImageByFileId(deleteImageIds);
            if(deleteImageFile < 1) throw new CustomException("이미지 삭제에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }

        return false;
    }

    @Override
    @Transactional
    public void deleteGoods(Long goodsId) {
        // TODO : 본인이 쓴 글만 삭제가능 (userId 정보와 굿즈글의 sellerId 비교 후 삭제)
        Long loginUserId = 1L;

        // 상태 체크
        AuctionStatus auctionStatus = goodsMapper.selectAuctionStatusByGoodsId(goodsId);
        if(auctionStatus == null) throw new NoSuchElementException("존재하지 않는 굿즈입니다.");

        // 진행중이 아닐 때만 삭제 (경매 대기 or 완료 일 때만 삭제가능)
        if(auctionStatus != AuctionStatus.PROCEEDING) {
            int deleteResult = goodsMapper.deleteGoods(goodsId, loginUserId);
            if(deleteResult < 1) throw new CustomException("굿즈 글 삭제에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);

        } else {
            throw new CustomException("경매 대기 또는 종료된 후에만 삭제가 가능합니다.", HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 사용자가 업로드한 파일명을 다른 사용자가 올린 파일명과 중복되지 않게 유니크한
     * 로컬 서버 저장용 파일명 생성하는 메서드
     * @param original 기존파일명
     * @return 로컬 서버 저장용 파일명
     */
    private String generateStoredFilename(String original) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "_" + original;
    }

    /**
     * 사용자 로컬에서 업로드된 파일들을 로컬 서버에 저장하는 메서드
     * @param file 사용자가 업로드한 파일
     * @param storedFilename 로직에서 만든 유니크한 fileName
     */
    private void saveFileToLocal(MultipartFile file, String storedFilename) {
        try {
            // 실제 저장될 물리 경로
            String fullPath = filePrefix + filePath;

            // 물리 경로에 굿즈이미지를 저장할 폴더가 없다면 만들기
            File dir = new File(fullPath);
            if (!dir.exists()) dir.mkdirs();

            // 로컬 개발환경 물리 경로에 저장
            File savedFile = new File(dir.getAbsolutePath(), storedFilename);

            if(!savedFile.exists()) {
                // MultipartFile 의 내용을 지정된 경로로 그대로 복사/저장함
                file.transferTo(savedFile);
                // 잘 저장되었는지 확인하기 위한 파일 사이즈 로그 찍기
                log.info("fileService.uploadFile -> fileSize :: " + file.getSize());
            }

        } catch (IOException e) {
            log.warn(e.getMessage());
            throw new CustomException("굿즈 이미지파일 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
