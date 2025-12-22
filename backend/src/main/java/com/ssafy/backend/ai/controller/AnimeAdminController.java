package com.ssafy.backend.ai.controller;

import com.ssafy.backend.ai.service.EmbeddingService;
import com.ssafy.backend.anime.service.AnimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/anime")
@RequiredArgsConstructor
public class AnimeAdminController {

    private final AnimeService animeService;
    private final EmbeddingService embeddingService;

    @PostMapping("/init")
    public String initAnime(@RequestParam(defaultValue = "200") int size) {

        animeService.bulkSyncAnimeFromTmdb(size);   // TMDB 데이터 대량 수집
        return "Anime init 성공";
    }

}

