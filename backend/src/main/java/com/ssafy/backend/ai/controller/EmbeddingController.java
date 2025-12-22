package com.ssafy.backend.ai.controller;

import com.ssafy.backend.ai.service.EmbeddingService;
import com.ssafy.backend.anime.service.AnimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmbeddingController {

    private final EmbeddingService embeddingService;
    private final AnimeService animeService;

    @PostMapping("/embedding/generate")
    public String generate() {
        embeddingService.generateEmbeddingsForAllAnime();
        return "Embedding generation completed!";
    }
}

