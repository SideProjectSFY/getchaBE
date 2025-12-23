package com.ssafy.backend.ai.controller;

import com.ssafy.backend.anime.service.AnimeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AnimeBulk API", description = "애니메이션 초기데이터 Insert API")
@RequestMapping("/admin/anime")
@RequiredArgsConstructor
public class AnimeAdminController {

    private final AnimeService animeService;

    @PostMapping("/init")
    public ResponseEntity<String> initAnime(@RequestParam(defaultValue = "200") int size) {

        animeService.bulkSyncAnimeFromTmdb(size);   // TMDB 데이터 대량 수집
        return ResponseEntity.ok("Anime init 성공");

    }

}

