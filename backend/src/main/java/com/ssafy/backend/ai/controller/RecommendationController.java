package com.ssafy.backend.ai.controller;

import com.ssafy.backend.ai.service.RecommendationService;
import com.ssafy.backend.anime.model.TmdbAnimeEntityDto;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/anime")
    public List<TmdbAnimeEntityDto> recommend(@AuthenticationPrincipal Long userId) throws IOException {
        return recommendationService.recommend(userId);
    }

    @GetMapping("/goods")
    public List<GoodsResponseDto.GoodsCard> recommendGoods(
            @AuthenticationPrincipal Long userId
    ) throws IOException {

        return recommendationService.recommendGoods(userId);
    }


}
