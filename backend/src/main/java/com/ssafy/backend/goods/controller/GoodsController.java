package com.ssafy.backend.goods.controller;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.goods.model.GoodsRequestDto;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import com.ssafy.backend.goods.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final GoodsService goodsService;

    @Operation(
            summary = "굿즈 등록",
            description = "새로운 굿즈 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "굿즈가 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "400", description = "굿즈 이미지파일 업로드에 실패하였습니다."),
            @ApiResponse(responseCode = "500", description = "굿즈 등록에 실패하였습니다.")
    })
    @RequestBody(content = @Content(
            encoding = @Encoding(name = "goodsRegister", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GoodsResponseDto.AddGoodsResult> postGoods(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestPart("goodsRegister") GoodsRequestDto.GoodsRegister goodsRegister,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        GoodsResponseDto.AddGoodsResult addGoods = goodsService.addGoods(loginUserId, goodsRegister, imageFiles);
        return ResponseEntity.ok(addGoods);
    }


    @Operation(
            summary = "굿즈 카드 목록 조회 (검색/필터)",
            description = "굿즈 목록을 조회합니다."
    )
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
            @ApiResponse(responseCode = "404", description = "존재하지 않는 굿즈입니다."),
    })
    @Parameter(name = "goodsId", description = "굿즈ID(pk)", required = true)
    @GetMapping
    public ResponseEntity<GoodsResponseDto.GoodsDetailAll> getGoodsById(
            @AuthenticationPrincipal Long loginUserId,
            @NotNull @RequestParam Long goodsId) {
        GoodsResponseDto.GoodsDetailAll goodsDetailAll = goodsService.getGoodsById(loginUserId, goodsId);
        return ResponseEntity.ok(goodsDetailAll);
    }

    @Operation(
            summary = "굿즈 글 정보 수정",
            description = "굿즈 글의 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "굿즈가 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "즉시구매가는 시작가 이상 500만원 이하여야 합니다." +
                    "or 경매 상태가 대기일 경우에만 수정이 가능합니다."),
            @ApiResponse(responseCode = "403", description = "수정 권한이 없습니다."),
            @ApiResponse(responseCode = "500", description = "굿즈 글 수정에 실패하였습니다."),
    })
    @RequestBody(content = @Content(
            encoding = @Encoding(name = "goodsModify", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateGoods(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestPart(value = "goodsModify") GoodsRequestDto.GoodsModify goodsModify,
            @RequestPart(value = "newImageFiles", required = false) List<MultipartFile> newImageFiles) {
        goodsService.updateGoods(loginUserId, goodsModify, newImageFiles);
        return new ResponseEntity<>("굿즈가 성공적으로 수정되었습니다.", HttpStatus.OK);
    }

    @Operation(
            summary = "굿즈 글 정보 삭제",
            description = "경매 상태가 대기 or 종료 일 때만 굿즈 글을 삭제할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "굿즈 글이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "경매 대기 또는 종료된 후에만 삭제가 가능합니다."),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 굿즈입니다."),
            @ApiResponse(responseCode = "503", description = "굿즈 글 삭제에 실패하였습니다")
    })
    @Parameter(name = "goodsId", description = "굿즈ID(pk)")
    @DeleteMapping
    public ResponseEntity<String> deleteGoods(@AuthenticationPrincipal Long loginUserId,
                                              @NotNull @RequestParam Long goodsId) {
        goodsService.deleteGoods(loginUserId, goodsId);
        return new ResponseEntity<>("굿즈 글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
    }

}
