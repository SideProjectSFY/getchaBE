package com.ssafy.backend.ai.controller;

import com.ssafy.backend.ai.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    @PostMapping("/api/embedding/generate")
    public String generate() {
        embeddingService.generateEmbeddingsForAllAnime(); //전체 애니 임베딩 생성
        return "Embedding generation completed!";
    }
}
