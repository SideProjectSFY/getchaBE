package com.ssafy.backend.goods.controller;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.goods.model.GoodsRequestDto;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import com.ssafy.backend.goods.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
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

    @Operation(
            summary = "굿즈 등록",
            description = "새로운 굿즈 정보를 등록합니다.")
//    @ApiResponses({}) // TODO : 작성예정
    @Parameters({
            @Parameter(name = "animeId", description = "애니ID(pk)"),
            @Parameter(name = "category", description = "카테고리", example = "figure"),
            @Parameter(name = "title", description = "굿즈 글 제목"),
            @Parameter(name = "description", description = "굿즈 상품 설명"),
            @Parameter(name = "startPrice", description = "시작가", example = "1000"),
            @Parameter(name = "instantBuyPrice", description = "즉시구매가", example = "5000"),
            @Parameter(name = "duration", description = "경매기간(일)", example = "3"),
    })
    @PostMapping
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
    @Parameters({
            @Parameter(name = "auctionStatus", description = "경매상태", example = "wait"),
            @Parameter(name = "category", description = "카테고리", example = "figure"),
    })
    @GetMapping("/list")
    public ResponseEntity<?> getAllGoods(@Valid @ModelAttribute GoodsRequestDto.GoodsLookUp goodsLookUp) {
        PageResponse<GoodsResponseDto.GoodsCard> allGoods = goodsService.getAllGoods(goodsLookUp);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "굿즈 글 상세 조회",
            description = "굿즈 글을 상세 조회합니다."
    )
    @Parameter(name = "goodsId", description = "굿즈ID(pk)", required = true)
    @GetMapping
    public ResponseEntity<?> getGoodsById(@NotBlank @RequestParam Long goodsId) {
        GoodsResponseDto.GoodsDetailAll result = goodsService.getGoodsById(goodsId);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "굿즈 글 정보 수정",
            description = "굿즈 글의 정보를 수정합니다."
    )
    @PutMapping
    public ResponseEntity<?> updateGoods(@Valid @RequestBody GoodsRequestDto.GoodsModify goodsModify, MultipartFile[] files) {
        boolean result = goodsService.updateGoods(goodsModify, files);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "굿즈 글 정보 삭제",
            description = "경매 상태가 대기 or 종료 일 때만 굿즈 글을 삭제할 수 있습니다."
    )
    @Parameter(name = "goodsId", description = "굿즈ID(pk)")
    @DeleteMapping
    public ResponseEntity<?> deleteGoods(@NotBlank @RequestParam Long goodsId) {
        boolean result = goodsService.deleteGoods(goodsId);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "굿즈 글 거래 중지",
            description = "판매자가 거래 중지 버튼을 클릭했을 경우 호출합니다")
    @Parameter(name = "goodsId", description = "굿즈ID(pk)", required = true)
    @PutMapping("/stop-auction")
    public ResponseEntity<?> updateAuctionStatus(@NotBlank @RequestParam Long goodsId) {
        boolean result = goodsService.updateAuctionStatus(goodsId, "stopped");
        return ResponseEntity.ok(null);
    }
}
