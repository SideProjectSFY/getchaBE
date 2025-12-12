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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @ApiResponse(responseCode = "400", description = "현재 최고 입찰자는 재입찰할 수 없습니다." +
                    "or 시작가 이상, 제한금액(500만원) 이하일 때 입찰이 가능합니다." +
                    "or 가상지갑 정보가 존재하지 않습니다." +
                    "or 입찰 금액만큼의 잔액이 부족합니다."),
            @ApiResponse(responseCode = "403", description = "판매자는 자신의 굿즈에 입찰할 수 없습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 굿즈 또는 이미 종료된 경매입니다."),
            @ApiResponse(responseCode = "500", description = "현재 최고가 여부 업데이트에 실패하였습니다." +
                    "or 입찰금액 등록에 실패하였습니다." +
                    "or 경매 상태 업데이트에 실패하였습니다." +
                    "or 가상화폐지갑 잔액/예치금 업데이트에 실패하였습니다." +
                    "or 지갑 거래 내역 기록에 실패하였습니다.")
    })
    @PostMapping
    public ResponseEntity<String> postBidForGoods(
            @AuthenticationPrincipal Long loginUserId,
            @RequestBody BidRequestDto.BidRegister bidRegister) {
        bidService.postBidForGoods(loginUserId, bidRegister);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("입찰이 완료되었습니다.");
    }

    @Operation(
            summary = "굿즈 글 거래 중지",
            description = "판매자가 거래 중지 버튼을 클릭했을 경우 호출합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거래 중지가 성공적으로 적용되었습니다."),
            @ApiResponse(responseCode = "403", description = "거래중지 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 굿즈 또는 이미 종료된 경매입니다."),
            @ApiResponse(responseCode = "500", description = "가상화폐지갑 잔액/예치금 업데이트에 실패하였습니다." +
                    "or 지갑 거래 내역 기록에 실패하였습니다." +
                    "or 경매 상태 업데이트에 실패하였습니다.")
    })
    @Parameter(name = "goodsId", description = "굿즈ID(pk)", required = true)
    @PutMapping("/stop-auction")
    public ResponseEntity<String> updateAuctionStatus(
            @AuthenticationPrincipal Long loginUserId,
            @NotNull @RequestParam Long goodsId) {
        bidService.updateStopAuctionStatus(loginUserId, goodsId);
        return ResponseEntity.ok("거래 중지가 성공적으로 적용되었습니다.");
    }
}
