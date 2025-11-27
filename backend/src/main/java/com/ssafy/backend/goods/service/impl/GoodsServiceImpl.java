package com.ssafy.backend.goods.service.impl;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.common.enums.Category;
import com.ssafy.backend.common.exception.CustomException;
import com.ssafy.backend.goods.model.*;
import com.ssafy.backend.goods.service.GoodsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class GoodsServiceImpl implements GoodsService {

    // 실제 서버 저장 경로 (TODO : 변경 필요)
    private final String uploadDir = "/upload/getcha/goods";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoods(GoodsRequestDto.GoodsRegister goodsRegister, List<MultipartFile> imageFiles) {

        System.out.println("들어오나? ");
        // 1. 카테고리 String -> 비교 후 Enum 타입으로 변경 (없으면 기타 / Enum 에서 내부적으로 처리)
        Category category = Category.getCategory(goodsRegister.getCategory());

        // 2. 경매기간 받아서, 종료일시 저장
        // 시간 맞춰야해서 따로 저장
        LocalDateTime createdAt = LocalDateTime.now();
        int duration = goodsRegister.getDuration();
        LocalDateTime auctionEndAt = createdAt.plusDays(duration);


        //금액 최소, 최대 체크할지 말지 고민 (프론트에서만 처리할까, 백엔드에서도 한번더 검증할까)

        Goods goods = Goods.builder()
                .sellerId(1L) // TODO : Token 파싱 후 넣을 userId
                .animeId(goodsRegister.getAnimeId())
                .category(category)
                .title(goodsRegister.getTitle())
                .description(goodsRegister.getDescription())
                .startPrice(goodsRegister.getStartPrice())
                .instantBuyPrice(goodsRegister.getInstantBuyPrice())
                .duration(duration)
                .auctionEndAt(auctionEndAt)
                .createdAt(createdAt)
                .build();

        // 3. 굿즈 등록
        int saveResult = goodsMapper.insertGoods(goods);
        // 굿즈 등록에 실패 시 에러 던지기
        if(saveResult < 1) throw new CustomException("굿즈 등록에 실패하였습니다", HttpStatus.INTERNAL_SERVER_ERROR);

        Long goodsId = goods.getId();

        // 4. 이미지 업로드
        int sortOrder = 1;
        for (MultipartFile file : imageFiles) {

            if (file.isEmpty()) continue;

            if(sortOrder > 5) break;

            String originalFilename = file.getOriginalFilename();
            String storedFilename = generateStoredFilename(originalFilename);

            // 실제 파일 저장
            saveFileToLocal(file, storedFilename);

            GoodsImage imageFile = GoodsImage.builder()
                    .goodsId(goodsId)
                    .originFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .filePath(uploadDir + "/" + storedFilename)
                    .fileSize(file.getSize())
                    .sortOrder(sortOrder++)
                    .build();

            // DB 저장
            goodsMapper.insertFiles(imageFile);
        }

    }

    /**
     * TODO
     * 1. 필수 !!!
     * - 토큰 정보 파싱 후 사용자 정보 받아오기
     */

    private final GoodsMapper goodsMapper;

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


    private String generateStoredFilename(String original) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "_" + original;
    }

    private void saveFileToLocal(MultipartFile file, String storedFilename) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            File savedFile = new File(dir, storedFilename);
            file.transferTo(savedFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}
