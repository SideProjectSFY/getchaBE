package com.ssafy.backend.goods.service.impl;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.common.enums.AuctionStatus;
import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.common.service.FileServie;
import com.ssafy.backend.goods.model.*;
import com.ssafy.backend.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;
    private final FileServie fileServie;
    private static final int LIMIT_AMOUNT = 5_000_000;

    // 실제 서버 저장 상대경로 filePath
    @Value("${file.upload.path}")
    private String filePath;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoods(
            Long loginUserId,
            GoodsRequestDto.GoodsRegister goodsRegister,
            List<MultipartFile> imageFiles) {

        // 시간 맞춰야해서 작성일시따로 저장
        LocalDateTime createdAt = LocalDateTime.now();
        // 경매기간 받아서, 종료일시 저장
        int duration = goodsRegister.getDuration();
        LocalDateTime auctionEndAt = createdAt.plusDays(duration);

        // 금액 검증
        Integer startPrice = goodsRegister.getStartPrice();
        Integer instantBuyPrice = goodsRegister.getInstantBuyPrice();
        if(instantBuyPrice != null &&
                instantBuyPrice < startPrice && instantBuyPrice > LIMIT_AMOUNT)
            throw new CustomException("즉시구매가는 시작가 이상 500만원 이하여야 합니다.", HttpStatus.BAD_REQUEST);

        Goods goods = Goods.builder()
                .sellerId(loginUserId)
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
        if(imageFiles != null || !imageFiles.isEmpty()) {
            int sortOrder = 1;

            for (MultipartFile file : imageFiles) {

                if(file.isEmpty()) continue;
                if(sortOrder > 5) break;

                // 실제 파일 저장 및 유니크한 파일명 반환
                String storedFilename = fileServie.saveFile(file);

                GoodsImage imageFile = GoodsImage.builder()
                        .goodsId(goodsId)
                        .originFilename(file.getOriginalFilename())
                        .storedFilename(storedFilename)
                        .filePath(filePath + "/" + storedFilename)
                        .fileSize(file.getSize())
                        .sortOrder(sortOrder++)
                        .build();

                // DB 저장
                goodsMapper.insertFiles(imageFile);
            }
        }

    }



    @Override
    @Transactional(readOnly = true)
    public PageResponse<GoodsResponseDto.GoodsCard> getAllGoods(GoodsRequestDto.GoodsLookUp goodsLookUp) {

        int page = goodsLookUp.getPage();
        int size = goodsLookUp.getSize();

        // 데이터 조회
        List<GoodsResponseDto.GoodsCard> goodsCardsList =
                Optional.ofNullable(goodsMapper.selectAllGoodsBySearch(goodsLookUp))
                        .orElse(Collections.emptyList()); // 값이 null 일 경우 빈 리스트 던짐

        long totalCount = goodsMapper.countGoods(goodsLookUp);
        // 굿즈 글이 0개 일 경우
        if(totalCount == 0) {
            return new PageResponse<>(Collections.emptyList(), page, 0, 0);
        }

        int totalPages = (int) Math.ceil((double) totalCount / size);

        // 총 페이지 수 보다 페이지 값이 크게 들어왔을 경우, 빈 리스트 보여주기
        if (page > totalPages) {
            return new PageResponse<>(Collections.emptyList(), page, totalPages, totalCount);
        }

        return new PageResponse<>(goodsCardsList, page, totalPages, totalCount);
    }


    @Override
    @Transactional(readOnly = true)
    public GoodsResponseDto.GoodsDetailAll getGoodsById(Long loginUserId, Long goodsId) {

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
    public void updateGoods(
            Long loginUserId,
            GoodsRequestDto.GoodsModify goodsModify,
            List<MultipartFile> newImageFiles) {

        Long goodsId = goodsModify.getGoodsId();
        int duration = goodsModify.getDuration();

        GoodsResponseDto.GoodsDetail goodsDetail = goodsMapper.selectGoodsById(goodsId, loginUserId);

        // 금액 검증
        Integer instantBuyPrice = goodsModify.getInstantBuyPrice();
        if(instantBuyPrice != null &&
                instantBuyPrice < goodsModify.getStartPrice() && instantBuyPrice > LIMIT_AMOUNT)
            throw new CustomException("즉시구매가는 시작가 이상 500만원 이하여야 합니다.", HttpStatus.BAD_REQUEST);

        // 경매 대기 상태 검증
        if(goodsDetail.getAuctionStatus() != AuctionStatus.WAIT)
            throw new CustomException("경매 상태가 대기일 경우에만 수정이 가능합니다.", HttpStatus.BAD_REQUEST);

        // 본인이 쓴글인지 검증
        if(!Objects.equals(goodsDetail.getSellerId(), loginUserId))
            throw new AccessDeniedException("수정 권한이 없습니다.");

        // 경매기간(duration) 수정이 될 경우를 대비하여, auction_end_at 도 작성일시를 기준으로 수정하기
        LocalDateTime auctionEndAt = goodsDetail.getCreatedAt().plusDays(duration);

        // 굿즈 데이터 수정
        int updateGoodsResult = goodsMapper.updateGoods(goodsModify, loginUserId, auctionEndAt);
        if(updateGoodsResult < 1)
            throw new CustomException("굿즈 글 수정에 실패하였습니다.", HttpStatus.SERVICE_UNAVAILABLE);


        /*
        *  이미지 리스트 수정
        * */

        // 1. 삭제 요청된 이미지 삭제 (DB + 실제 파일 Hard 삭제)
        List<Long> deleteImageIds = goodsModify.getDeleteImageIds();
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            List<GoodsImage> deleteImages = goodsMapper.selectDeleteImageFileByFileId(deleteImageIds);

            // 실제 로컬에서 파일 삭제 (용량 문제로 hard 삭제처리)
            for (GoodsImage img : deleteImages) {
                fileServie.deleteFile(img.getStoredFilename());
            }

            // DB 에서 파일 row 삭제
            int deleteImageFile = goodsMapper.deleteGoodsImageByFileId(deleteImageIds);
            if(deleteImageFile < 1) throw new CustomException("이미지 삭제에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. 유지되는 기존 이미지들의 sort_order 업데이트
        if (goodsModify.getExistingImages() != null && !goodsModify.getExistingImages().isEmpty()) {
            int updateImageSortOrder = goodsMapper.updateImageSortOrder(goodsId, goodsModify.getExistingImages());
            if(updateImageSortOrder < 1)
                throw new CustomException("기존 이미지 순서 업데이트에 실패하였습니다.", HttpStatus.BAD_REQUEST);
        }


        // 3. 신규 이미지 업로드 & INSERT
        if (newImageFiles != null && !newImageFiles.isEmpty()) {
            for (MultipartFile file : newImageFiles) {

                // 파일명 생성
                String storedFilename = fileServie.saveFile(file);

                // sort_order: 기존 이미지 최대값 + 1
                Integer nextOrder = goodsMapper.selectNextSortOrder(goodsId);

                GoodsImage imageFile = GoodsImage.builder()
                        .goodsId(goodsId)
                        .originFilename(file.getOriginalFilename())
                        .storedFilename(storedFilename)
                        .filePath(filePath + "/" + storedFilename)
                        .fileSize(file.getSize())
                        .sortOrder(nextOrder)
                        .build();

                // DB 저장
                goodsMapper.insertFiles(imageFile);
            }
        }

    }

    @Override
    @Transactional
    public void deleteGoods(Long loginUserId, Long goodsId) {

        GoodsResponseDto.GoodsDetail goodsDetail = goodsMapper.selectGoodsById(goodsId, loginUserId);
        if(!Objects.equals(goodsDetail.getSellerId(), loginUserId)) {
            throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
        }

        // 상태 체크
        AuctionStatus auctionStatus = goodsDetail.getAuctionStatus();
        if(auctionStatus == null) throw new NoSuchElementException("존재하지 않는 굿즈입니다.");

        // 진행중이 아닐 때만 삭제 (경매 대기 or 완료 일 때만 삭제가능)
        if(auctionStatus != AuctionStatus.PROCEEDING) {

            int deleteResult = goodsMapper.deleteGoods(goodsId, loginUserId);
            if(deleteResult < 1) throw new CustomException("굿즈 글 삭제에 실패하였습니다", HttpStatus.SERVICE_UNAVAILABLE);

        } else {
            throw new CustomException("경매 대기 또는 종료된 후에만 삭제가 가능합니다.", HttpStatus.BAD_REQUEST);
        }
    }

}
