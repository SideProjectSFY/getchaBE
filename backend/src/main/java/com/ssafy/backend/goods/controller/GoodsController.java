package com.ssafy.backend.goods.controller;

import com.ssafy.backend.common.ApiResponse;
import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.goods.model.GoodsRequestDto;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import com.ssafy.backend.goods.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Goods Rest API
 */

@RequestMapping("/goods")
@Tag(name = "Goods API", description = "굿즈 등록, 조회, 수정, 삭제 API")
@RestController
@AllArgsConstructor
public class GoodsController {

    // TODO : 추후 반환값 수정 + 예외처리 추가

    private final GoodsService goodsService;

//    public GoodsController(GoodsService goodsService) {
//        this.goodsService = goodsService;
//    }


    @Operation(
            summary = "굿즈 등록",
            description = "새로운 굿즈 정보를 등록합니다.",
            tags = {"Goods"}
    )
//    @ApiResponses({}) // TODO : 작성예정
//    @PostMapping
    public ResponseEntity<?> postGoods(@Valid @RequestBody GoodsRequestDto.GoodsRegister goodsRegister, MultipartFile[] files) {
        // TODO : 결과값은 추후 필요 시 수정
        boolean result = goodsService.addGoods(goodsRegister, files);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(null);
    }

    @Operation(
            summary = "굿즈 카드 목록 조회 (검색/필터)",
            description = "굿즈 목록을 조회합니다."
    )
//    @GetMapping
    public ResponseEntity<?> getAllGoods(@Valid @RequestBody GoodsRequestDto.GoodsLookUp goodsLookUp) {
        PageResponse<GoodsResponseDto.GoodsCard> allGoods = goodsService.getAllGoods(goodsLookUp);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "굿즈 글 상세 조회",
            description = "굿즈 글을 상세 조회합니다."
    )
//    @GetMapping
    public ResponseEntity<?> getGoodsById(@Valid @RequestParam Long goodsId) {
        GoodsResponseDto.GoodsDetailAll result = goodsService.getGoodsById(goodsId);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "굿즈 글 정보 수정",
            description = "굿즈 글의 정보를 수정합니다."
    )
//    @PutMapping
    public ResponseEntity<?> updateGoods(@Valid @RequestBody GoodsRequestDto.GoodsModify goodsModify, MultipartFile[] files) {
        boolean result = goodsService.updateGoods(goodsModify, files);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "굿즈 글 정보 삭제",
            description = "경매 상태가 대기 or 종료 일 때만 굿즈 글을 삭제할 수 있습니다."
    )
//    @DeleteMapping
    public ResponseEntity<?> deleteGoods(@Valid @RequestParam Long goodsId) {
        boolean result = goodsService.deleteGoods(goodsId);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "굿즈 경매 상태 업데이트",
            description = "굿즈 경매 상태 업데이트합니다.")
//    @PutMapping("/")
    public ResponseEntity<?> updateAuctionStatus(@Valid @RequestParam Long goodsId, @RequestParam String auctionStatus) {
        boolean result = goodsService.updateAuctionStatus(goodsId, auctionStatus);
        return ResponseEntity.ok(null);
    }
}
