package com.ssafy.backend.wish.controller;
import com.ssafy.backend.wish.model.WishResponseDto;
import com.ssafy.backend.wish.service.WishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


/**
 * Wish Rest API
 */

@RequestMapping("/wish")
@Tag(name = "Wish API", description = "찜 하기, 찜 취소 API")
@RestController
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @Operation(
            summary = "찜 등록",
            description = "굿즈를 찜 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "찜이 성공적으로 등록되었습니다."),
            @ApiResponse(responseCode = "403", description = "본인의 굿즈는 찜할 수 없습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 굿즈입니다."),
            @ApiResponse(responseCode = "409", description = "이미 찜한 굿즈입니다."),
            @ApiResponse(responseCode = "500", description = "찜 등록하는 것에 실패하였습니다")
    })
    @PostMapping
    public ResponseEntity<WishResponseDto.AddWishResult> addWish(
            @AuthenticationPrincipal Long loginUserId,
            @RequestParam Long goodsId) {
        WishResponseDto.AddWishResult addWish = wishService.addWish(goodsId, loginUserId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addWish);
    }


    @Operation(
            summary = "찜 취소",
            description = "굿즈를 찜 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜이 성공적으로 취소되었습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 굿즈입니다."),
            @ApiResponse(responseCode = "500", description = "찜 취소하는 것에 실패하였습니다")
    })
    @DeleteMapping
    public ResponseEntity<WishResponseDto.DeleteWishResult> deleteWish(
            @AuthenticationPrincipal Long loginUserId,
            @RequestParam Long goodsId) {
        WishResponseDto.DeleteWishResult deleteWish = wishService.deleteWish(goodsId, loginUserId);
        return ResponseEntity.ok(deleteWish);
    }


}
