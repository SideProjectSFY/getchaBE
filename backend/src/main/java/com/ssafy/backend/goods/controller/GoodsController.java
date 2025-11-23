package com.ssafy.backend.goods.controller;

import com.ssafy.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Goods Rest API
 */

@RequestMapping("/goods")
@Tag(name = "Goods API", description = "굿즈 등록, 조회, 수정, 삭제 API")
@RestController
public class GoodsController {

    // TODO : 수정 예정

    /**
     * 굿즈 등록 API
     */
    @Operation(summary = "굿즈 등록", description = "")
    @PostMapping
    public ResponseEntity<?> postGoods() {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(null));
    }

    /**
     * 전체 굿즈 조회 API
     */
    @Operation(summary = "전체 굿즈 조회", description = "")
    @GetMapping
    public ResponseEntity<?> getAllGoods() {
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

}
