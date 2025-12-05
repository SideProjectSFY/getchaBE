package com.ssafy.backend.bid.controller;

import com.ssafy.backend.bid.model.BidRequestDto;
import com.ssafy.backend.bid.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            summary = "굿즈 글 거래 중지",
            description = "판매자가 거래 중지 버튼을 클릭했을 경우 호출합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거래 중지가 성공적으로 적용되었습니다."),
            @ApiResponse(responseCode = "400", description = "거래중지는 판매자만 가능합니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 굿즈 또는 이미 종료된 경매입니다."),
            @ApiResponse(responseCode = "503", description = "거래 중지를 실패하였습니다.")
    })
    @Parameter(name = "goodsId", description = "굿즈ID(pk)", required = true)
    @PutMapping("/stop-auction")
    public ResponseEntity<String> updateAuctionStatus(@NotNull @RequestParam Long goodsId) {
        bidService.updateStopAuctionStatus(goodsId);
        return new ResponseEntity<>("거래 중지가 성공적으로 적용되었습니다.", HttpStatus.OK);
    }
}
