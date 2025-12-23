package com.ssafy.backend.ai.controller;

import com.ssafy.backend.ai.model.RecommendedResponseDto;
import com.ssafy.backend.ai.service.RecommendationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Recommend API", description = "사용자 맞춤 추천 애니메이션 기반 굿즈 추천 AI API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/anime")
    public List<RecommendedResponseDto.RecommendedGoods> recommend(@AuthenticationPrincipal Long userId) throws IOException {
        return recommendationService.recommend(userId);
    }

    @GetMapping("/goods")
    public List<RecommendedResponseDto.RecommendedGoods> recommendGoods(
            @AuthenticationPrincipal Long userId
    ) throws IOException {
        return recommendationService.recommendGoodsWithMatch(userId);
    }


}
