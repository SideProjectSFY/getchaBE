package com.ssafy.backend.anime.controller;

import com.ssafy.backend.anime.model.AnimeRequestDto;
import com.ssafy.backend.anime.model.AnimeResponseDto;
import com.ssafy.backend.anime.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AnimeSearch", description = "애니메이션 검색")
@RestController
@RequestMapping("/anime")
@RequiredArgsConstructor
public class AnimeController {

    private final AnimeService animeService;

    @GetMapping("/search")
    @Operation(summary = "애니메이션 검색", description = "애니메이션 제목을 입력해주세요.")
    public ResponseEntity<List<AnimeResponseDto>> serarchAnime(@ModelAttribute AnimeRequestDto request) {
        List<AnimeResponseDto> result = animeService.searchAndSyncAnime(request);
        return ResponseEntity.ok(result);
    }
}
