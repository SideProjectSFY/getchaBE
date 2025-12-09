package com.ssafy.backend.wallet.controller;

import com.ssafy.backend.common.PageResponse;
import com.ssafy.backend.wallet.model.WalletRequestDto;
import com.ssafy.backend.wallet.model.WalletResponseDto;
import com.ssafy.backend.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<PageResponse<WalletResponseDto.WalletHistoryAll>> getAllWalletHistory(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @ModelAttribute WalletRequestDto.SearchWalletHistory searchHistory
    ) {
        PageResponse<WalletResponseDto.WalletHistoryAll> allWalletHistory = walletService.getAllWalletHistory(loginUserId, searchHistory);
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
    @PostMapping("/charge")
    public ResponseEntity<Integer> chargeCoin(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody WalletRequestDto.ChargeCoinAmount chargeCoinAmount) {
        Integer balance = walletService.chargeCoin(loginUserId, chargeCoinAmount);
        return ResponseEntity.ok(balance);
    }


}
