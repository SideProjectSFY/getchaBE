package com.ssafy.backend.anime.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.anime.model.AnimeMapper;
import com.ssafy.backend.anime.model.AnimeRequestDto;
import com.ssafy.backend.anime.model.AnimeResponseDto;
import com.ssafy.backend.anime.model.TmdbAnimeEntityDto;
import com.ssafy.backend.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AnimeServiceImpl implements AnimeService {

    private final AnimeMapper animeMapper;

    // 외부 API 호출용 HTTP 클라이언트
    private final RestTemplate restTemplate = new RestTemplate();
    // JSON 파싱
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.base-url}")
    private String tmdbUrl;

    // tmdb poster_path 는 상대경로라서 앞에 붙여주기 !
    @Value("${tmdb.image-base-url:https://image.tmdb.org/t/p/w500}")
    private String posterBaseUrl;

    @Override
    public List<AnimeResponseDto> searchAndSyncAnime(AnimeRequestDto request) {
                        // 공백 trim
        String keyword = normalizeKeyword(request.getKeyword());

        // 1. DB 검색
        List<TmdbAnimeEntityDto> localList = animeMapper.findAnimeByKeyword(keyword);

        // 2. DB 내 결과가 없고 2글자 이상일 경우 TMDB 호출
        if (localList.isEmpty()) {
            if (keyword.length() < 2) {
                throw new CustomException("검색어는 2글자 이상 입력해주세요.", HttpStatus.BAD_REQUEST);
            }

            List<TmdbAnimeEntityDto> searchAnimeFromTmdb = searchFromTmdb(keyword);

            if (searchAnimeFromTmdb.isEmpty()) {
                throw new CustomException("존재하지 않는 애니메이션입니다.", HttpStatus.NOT_FOUND);
            }

            // DB 저장 + 장르 저장
            for(TmdbAnimeEntityDto anime : searchAnimeFromTmdb){
                // 1) tmdb_anime 저장
                animeMapper.insertAnime(anime);

                // 2) anime_genre 저장
                if(anime.getGenreIds() != null){
                    for(Integer genreId : anime.getGenreIds()){

                        // 애니메이션 장르(16)은 공통이므로 저장 X
                        if(genreId == 16){
                            continue;
                        }
                        animeMapper.insertAnimeGenre(anime.getId(), genreId);
                    }
                }
            }

            // 저장 후 다시 List 로 반환
            localList = animeMapper.findAnimeByKeyword(keyword);
        }

        if (localList.isEmpty()) {
            throw new CustomException("존재하지 않는 애니메이션입니다.", HttpStatus.NOT_FOUND);
        }

        return localList.stream()
                .map(a -> new AnimeResponseDto(
                        a.getId(),
                        a.getTitle(),
                        a.getPosterUrl()
                ))
                .toList();
    }


    // TMDB API 호출
    private List<TmdbAnimeEntityDto> searchFromTmdb(String keyword) {

        String url = tmdbUrl
                + "?query=" + UriUtils.encode(keyword, StandardCharsets.UTF_8)
                + "&api_key=" + apiKey
                + "&language=ko-KR";

        // JSON 응답을 String으로 받기
        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            return List.of();
        }

        // JSON 중 results 하위만 추출
        // path 는 값이 없을 때 null 대신 MissingNode
        JsonNode results = parse(response).path("results");
        if (!results.isArray() || results.isEmpty()) {
            return List.of();
        }

        List<TmdbAnimeEntityDto> list = new ArrayList<>();

        for (JsonNode node : results) {

            // 1. 애니메이션 장르(16) 포함 여부 체크
            if (!containsAnimationGenre(node.path("genre_ids"))) {
                continue;
            }

            long tmdbId = node.path("id").asLong();

            // 2. 상세 정보 조회
            JsonNode detail = searchDetail(tmdbId);

            //3. genre_ids 배열 -> List<Integer> 로 파싱
            JsonNode genreIdsNode = node.path("genre_ids");
            List<Integer> genreIds = new ArrayList<>();
            if(genreIdsNode.isArray()){
                for(JsonNode genre : genreIdsNode){
                    genreIds.add(genre.asInt());
                }
            }

            // 4. Dto 생성
            TmdbAnimeEntityDto anime = TmdbAnimeEntityDto.builder()
                    .id(tmdbId)
                    .title(resolveTitle(detail))
                    .posterUrl(resolvePosterUrl(node, detail))
                    .overview(resolveOverview(node, detail))
                    .voteAverage(Math.round(detail.path("vote_average").asDouble(0) * 100) / 100.0)
                    .voteCount(detail.path("vote_count").asLong(0))
                    .popularity(Math.round(detail.path("popularity").asDouble(0) * 100) / 100.0)
                    .genreIds(genreIds)
                    .build();

            list.add(anime);
        }

        return list;
    }



    // TMDB 상세 정보 조회
    private JsonNode searchDetail(Long tmdbId) {

        String detailType = tmdbUrl.toLowerCase().contains("/movie")
                ? "movie"
                : "tv";

        String url = "https://api.themoviedb.org/3/" + detailType + "/" + tmdbId
                + "?api_key=" + apiKey
                + "&language=ko-KR";

        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            return objectMapper.createObjectNode();
        }
        return parse(response);
    }

    // 문자열 JSON -> JsonNode 로 파싱
    private JsonNode parse(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new CustomException("TMDB 응답 처리 중 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY);
        }
    }

    // TMDB 상세 정보에서 제목 꺼내기
    private String resolveTitle(JsonNode detail) {
        // 영화면 title
        String title = detail.path("title").asText("");
        if (!title.isBlank()) {
            return title;
        }
        // TV면 name
        return detail.path("name").asText("");
    }

    // TMDB 상세 정보에서 포스터 url 꺼내기
    private String resolvePosterUrl(JsonNode primary, JsonNode fallback) {
        String posterPath = primary.path("poster_path").asText("");

        // poster_path에 없으면 상세 정보의 poster_path 가져오기
        if (posterPath.isBlank() && fallback != null) {
            posterPath = fallback.path("poster_path").asText("");
        }

        if (posterPath.isBlank()) {
            return null;
        }

        // 상대경로가 아닌, http로 시작하면 그대로 사용
        if (posterPath.startsWith("http")) {
            return posterPath;
        }

        return posterBaseUrl + posterPath;
    }

    // TMDB 상세 정보에서 줄거리 꺼내기
    private String resolveOverview(JsonNode primary, JsonNode fallback) {
        String overview = primary.path("overview").asText("");

        //overview에 없으면 상세 정보의 overview 가져오기
        if (overview.isBlank() && fallback != null) {
            overview = fallback.path("overview").asText("");
        }

        overview = overview.trim();
        return overview.isEmpty() ? null : overview;
    }

    // genre_ids 에 애니메이션 장르 16 이 포함되어 있는 지 체크
    private boolean containsAnimationGenre(JsonNode genreIds) {
        if (!genreIds.isArray()) {
            return false;
        }
        for (JsonNode idNode : genreIds) {
            if (idNode.asInt() == 16) {
                return true;
            }
        }
        return false;
    }

    // 공백 trim
    private String normalizeKeyword(String keyword) {
        String trimmed = Objects.toString(keyword, "").trim();
        if (trimmed.isEmpty()) {
            throw new CustomException("검색어를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }
        return trimmed;
    }

    public void bulkSyncAnimeFromTmdb(int maxPage) {

        for (int page = 1; page <= maxPage; page++) {

            String url = tmdbUrl
                    + "&with_genres=16"
                    + "&page=" + page
                    + "&language=ko-KR"
                    + "&api_key=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);
            if (response == null) break;

            JsonNode results = parse(response).path("results");
            if (!results.isArray() || results.isEmpty()) break;

            for (JsonNode node : results) {

                long tmdbId = node.path("id").asLong();

                // 상세 정보 (제목, 줄거리, 평점 등을 위해)
                JsonNode detail = searchDetail(tmdbId);

                // ✅ 핵심: detail이 아닌 node의 genre_ids 사용!
                JsonNode genreIdsNode = node.path("genre_ids");
                List<Integer> genreIds = new ArrayList<>();
                if(genreIdsNode.isArray()){
                    for(JsonNode genre : genreIdsNode){
                        int gid = genre.asInt();
                        if (gid != 16) {
                            genreIds.add(gid);
                        }
                    }
                }

                TmdbAnimeEntityDto anime = TmdbAnimeEntityDto.builder()
                        .id(tmdbId)
                        .title(resolveTitle(detail))
                        .posterUrl(resolvePosterUrl(node, detail))
                        .overview(resolveOverview(node, detail))
                        .voteAverage(detail.path("vote_average").asDouble())
                        .voteCount(detail.path("vote_count").asLong())
                        .popularity(detail.path("popularity").asDouble())
                        .genreIds(genreIds)
                        .build();

                animeMapper.insertAnime(anime);

                for (Integer gid : genreIds) {
                    animeMapper.insertAnimeGenre(anime.getId(), gid);
                }

                // ✅ 추가: 각 작품마다 딜레이 (Rate limit 방지)
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }

            // 페이지 간 딜레이
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
    }
}
