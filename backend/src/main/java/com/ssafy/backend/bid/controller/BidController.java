package com.ssafy.backend.bid.controller;

import com.ssafy.backend.bid.model.BidRequestDto;
import com.ssafy.backend.bid.model.BidResponseDto;
import com.ssafy.backend.bid.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/bid")
@Tag(name = "Bid API", description = "입찰 등록, 입찰 참여자 조회 API")
@RequiredArgsConstructor
@RestController
public class BidController {

    private final BidService bidService;

    @Operation(
            summary = "굿즈 입찰",
            description = "굿즈에 대해 입찰을 수행합니다. 즉시구매가 이상이면 즉시 낙찰 처리됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "입찰이 완료되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 검증 오류"),
            @ApiResponse(responseCode = "503", description = "입찰 처리 실패")
    })
    @PostMapping
    public ResponseEntity<String> postBidForGoods(@RequestBody BidRequestDto.BidRegister bidRegister) {
        bidService.postBidForGoods(bidRegister);
        return new ResponseEntity<>("입찰이 완료되었습니다.", HttpStatus.CREATED);
    }

    @Operation(
            summary = "굿즈 입찰 참여자 리스트 조회",
            description = "굿즈에 입찰한 참여자 리스트를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "존재하지 않는 굿즈 입니다."),
    })
    @GetMapping
    public ResponseEntity<List<BidResponseDto.BidParticipant>> getAllParticipant(@NotNull @RequestParam Long goodsId) {
        List<BidResponseDto.BidParticipant> resultList = bidService.getAllParticipant(goodsId);
        return ResponseEntity.ok(resultList);

    }
}
