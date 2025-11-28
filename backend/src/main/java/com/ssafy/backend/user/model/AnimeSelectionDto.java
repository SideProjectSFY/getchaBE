package com.ssafy.backend.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimeSelectionDto {
    private Long animeId;
    private String title;
    private String posterUrl;
}
