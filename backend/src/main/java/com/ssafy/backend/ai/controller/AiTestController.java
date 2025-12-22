package com.ssafy.backend.ai.controller;

import com.ssafy.backend.ai.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiTestController {

    private final RecommendationService recommendationService;

    @GetMapping("/test")
    public Object test(@RequestParam Long userId) throws Exception {
        return recommendationService.recommend(userId);
    }
}
