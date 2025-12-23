package com.ssafy.backend.ai.controller;

import com.ssafy.backend.ai.service.EmbeddingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Embedding API", description = "임베딩 생성 API")
@RestController
@RequestMapping("/embedding")
@RequiredArgsConstructor
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    @PostMapping("/generate")
    public ResponseEntity<String> generate() {
        embeddingService.generateEmbeddingsForAllAnime();
        return ResponseEntity.ok("임베딩 생성 성공");
    }
}

