package com.ssafy.backend.wallet.controller;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.wallet.model.WalletHistory;
import com.ssafy.backend.wallet.model.WalletResponseDto;
import com.ssafy.backend.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/wallet")
@Tag(name = "Wallet API", description = "거래 내역, 지갑 조회, 코인 충전 API")
@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Operation(
            summary = "거래 내역 목록 조회",
            description = "거래 내역 목록 조회합니다."
    )
    @GetMapping("/history")
    public ResponseEntity<PageResponse<WalletHistory>> getAllWalletHistory(
            @AuthenticationPrincipal Long loginUserId
            // TODO : RequestDTO 작성 예정
    ) {
        PageResponse<WalletHistory> allWalletHistory = walletService.getAllWalletHistory(loginUserId);
        return ResponseEntity.ok(allWalletHistory);
    }

    @Operation(
            summary = " 자산 현황 조회",
            description = "자산 현황을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자산 현황을 성공적으로 조회하였습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 또는 지갑이 없습니다."),
    })
    @GetMapping
    public ResponseEntity<WalletResponseDto.CoinWalletStatus> getCoinWallet(@AuthenticationPrincipal Long loginUserId) {
        WalletResponseDto.CoinWalletStatus coinWalletStatus = walletService.getCoinWalletStatus(loginUserId);
        return ResponseEntity.ok(coinWalletStatus);
    }


    @Operation(
            summary = "코인 충전 (임시)",
            description = "코인을 충전합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "코인이 성공적으로 충전되었습니다."),
            @ApiResponse(responseCode = "400", description = "충전금액은 양수여야 합니다."),
            @ApiResponse(responseCode = "500", description = "코인 충전에 실패했습니다."),
    })
    @PostMapping
    public ResponseEntity<String> chargeCoin(
            @AuthenticationPrincipal Long loginUserId,
            @RequestBody Integer coinWallet) {
        walletService.chargeCoin(loginUserId, coinWallet);
        return new ResponseEntity<>("코인이 성공적으로 충전되었습니다.", HttpStatus.CREATED);
    }


}
