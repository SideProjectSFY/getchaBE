package com.ssafy.backend.goods.controller;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.goods.model.GoodsRequestDto;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import com.ssafy.backend.goods.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * Goods Rest API
 */

@RequestMapping("/goods")
@Tag(name = "Goods API", description = "굿즈 등록, 조회, 수정, 삭제 API")
@RestController
@RequiredArgsConstructor
public class GoodsController {

    // TODO : 추후 반환값 수정 + 예외처리 추가

    private final GoodsService goodsService;

    @Operation(
            summary = "굿즈 등록",
            description = "새로운 굿즈 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "굿즈가 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "400", description = "굿즈 이미지파일 업로드에 실패하였습니다."),
            @ApiResponse(responseCode = "503", description = "굿즈 등록에 실패하였습니다.")
    })
    @PostMapping
    public ResponseEntity<String> postGoods(@Valid @RequestPart(value = "goodRegister") GoodsRequestDto.GoodsRegister goodsRegister,
                                            @RequestPart(value = "imageFiles", required = false)
                                            @Schema(type = "array", example = "파일", description = "다중 이미지 업로드",
                                            implementation = MultipartFile.class) List<MultipartFile> imageFiles) {
        goodsService.addGoods(goodsRegister, imageFiles);
        return new ResponseEntity<>("굿즈가 성공적으로 등록되었습니다.",HttpStatus.CREATED);
    }

    @Operation(
            summary = "굿즈 카드 목록 조회 (검색/필터)",
            description = "굿즈 목록을 조회합니다."
    )
    @Parameters({
            @Parameter(name = "page", description = "페이지", example = "1"),
            @Parameter(name = "size", description = "크기", example = "10"),
            @Parameter(name = "sort", description = "정렬기준", examples = {
                    @ExampleObject(name = "현재입찰가순(높은순)", value = "price"),
                    @ExampleObject(name = "찜 수(많은순)", value = "wish"),
                    @ExampleObject(name = "날짜순(현재일자와 가장 가까운 경매종료일자순)/default", value = "auctionEndAt")
            }),
            @Parameter(name = "searchName", description = "검색명(글 제목 or 애니메이션 제목)", examples = {
                    @ExampleObject(name = "글 제목", value = "나루토 피규어 모양"),
                    @ExampleObject(name = "애니메이션 제목", value = "나루토")
            }),
            @Parameter(name = "auctionStatus", description = "경매상태", examples = {
                    @ExampleObject(name = "대기", value = "WAIT"),
                    @ExampleObject(name = "진행중", value = "PROCEEDING"),
                    @ExampleObject(name = "낙찰", value = "COMPLETED"),
                    @ExampleObject(name = "패찰", value = "STOPPED"),
            }),
            @Parameter(name = "category", description = "카테고리", example = "figure", examples = {
                    @ExampleObject(name = "피규어", value = "FIGURE"),
                    @ExampleObject(name = "포토카드", value = "PHOTOCARD"),
                    @ExampleObject(name = "아크릴스탠드", value = "ACRYLICSTAND"),
                    @ExampleObject(name = "키링", value = "KEYRING"),
                    @ExampleObject(name = "인형", value = "DOLL"),
                    @ExampleObject(name = "포스터", value = "POSTER"),
                    @ExampleObject(name = "뱃지류", value = "BADGE"),
                    @ExampleObject(name = "기타", value = "OTHER")
            })
    })
    @GetMapping("/list")
    public ResponseEntity<PageResponse<GoodsResponseDto.GoodsCard>> getAllGoods(@Valid @ModelAttribute GoodsRequestDto.GoodsLookUp goodsLookUp) {
        PageResponse<GoodsResponseDto.GoodsCard> allGoods = goodsService.getAllGoods(goodsLookUp);
        return ResponseEntity.ok(allGoods);
    }

    @Operation(
            summary = "굿즈 글 상세 조회",
            description = "굿즈 글을 상세 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "굿즈가 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "403", description = "존재하지 않는 굿즈입니다."),
    })
    @Parameter(name = "goodsId", description = "굿즈ID(pk)", required = true)
    @GetMapping
    public ResponseEntity<GoodsResponseDto.GoodsDetailAll> getGoodsById(@NotNull @RequestParam Long goodsId) {
        GoodsResponseDto.GoodsDetailAll goodsDetailAll = goodsService.getGoodsById(goodsId);
        return ResponseEntity.ok(goodsDetailAll);
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "굿즈 글이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "경매 대기 또는 종료된 후에만 삭제가 가능합니다."),
            @ApiResponse(responseCode = "503", description = "굿즈 글 삭제에 실패하였습니다")
    })
    @Parameter(name = "goodsId", description = "굿즈ID(pk)")
    @DeleteMapping
    public ResponseEntity<String> deleteGoods(@NotNull @RequestParam Long goodsId) {
        goodsService.deleteGoods(goodsId);
        return new ResponseEntity<>("굿즈 글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
    }

}
