package com.ssafy.backend.ai.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ssafy.backend.ai.model.RecommendedResponseDto;
import com.ssafy.backend.anime.model.AnimeMapper;
import com.ssafy.backend.anime.model.TmdbAnimeEntityDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    @Value("${pinecone.api.key}")
    private String pineconeKey;

    @Value("${pinecone.index.host}")
    private String pineconeHost;

    @Value("${huggingface.api.token}")
    private String hfToken;

    @Value("${huggingface.api.url}")
    private String hfRouterUrl;



    private final AnimeMapper animeMapper;
    private final Gson gson = new Gson();
    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Hugging Face Inference API로 임베딩 생성
     */
    private List<Double> createEmbedding(String text) throws IOException {

        // 1) 최신 HF Router Inference URL
        String apiUrl = hfRouterUrl;

        // 2) 요청 바디 - inputs만!
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("inputs", text); // string 하나

        RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + hfToken)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            String respBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                // 디버깅을 위해 상태코드 + 원문 같이 보기
                throw new IOException("HF API Error: code=" + response.code() + ", body=" + respBody);
            }

            // 3) 응답은 2차원 배열이 아닌 1차원 배열 형태로 온다!
            String json = respBody;


            JsonArray emb = gson.fromJson(json, JsonArray.class);

            List<Double> vector = new ArrayList<>();
            for (int i = 0; i < emb.size(); i++) {
                vector.add(emb.get(i).getAsDouble());
            }

            return vector;

        }
    }

    /**
     * 단일 애니 임베딩 생성
     */
    public List<Double> getAnimeEmbedding(Long animeId) throws IOException {

        // 애니메이션 ID 를 통해 애니메이션 데이터 조회
        TmdbAnimeEntityDto animeDto = animeMapper.findByIdForEmbedding(animeId);

        String overview = animeDto.getOverview();
        if (overview == null || overview.isBlank()) {
            overview = "No overview available.";
        }

        // 애니메이션 ID 를 통해 해당 애니메이션의 장르 종류 조회
        List<String> genres = animeMapper.findGenresById(animeId);
        String genreText = genres.isEmpty() ? "Unknown" : String.join(", ", genres);

        StringBuilder sb = new StringBuilder("passage : ").append(animeDto.getTitle())
                .append("\n OverView : ").append(overview)
                .append("\n Genres : ").append(genreText);

        return createEmbedding(sb.toString());
    }


    /**
     * 가중 평균 유저 벡터 생성하는 메서드
     * */
    public List<Double> weightedUserEmbedding(
            List<List<Double>> vectors,
            List<Double> weights
    ) {
        int size = vectors.get(0).size();
        List<Double> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            double sum = 0;
            for (int j = 0; j < vectors.size(); j++) {
                sum += vectors.get(j).get(i) * weights.get(j);
            }
            result.add(sum);
        }
        return result;
    }

    /**
     * Pinecone에 벡터 저장
     */
    public void saveVectorToPinecone(
            Long id,
            List<Double> vector,
            String title,
            String genreText,
            double popularity
    ) throws IOException {

        JsonObject vectorObj = new JsonObject();
        vectorObj.addProperty("id", String.valueOf(id));

        JsonArray valuesArray = new JsonArray();
        for (Double val : vector) {
            valuesArray.add(val.floatValue());
        }
        vectorObj.add("values", valuesArray);

        // metadata 추가
        JsonObject metadata = new JsonObject();
        metadata.addProperty("title", title);
        metadata.addProperty("genres", genreText);
        metadata.addProperty("popularity", popularity);

        vectorObj.add("metadata", metadata);

        JsonArray vectorsArray = new JsonArray();
        vectorsArray.add(vectorObj);

        JsonObject requestBody = new JsonObject();
        requestBody.add("vectors", vectorsArray);

        RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://" + pineconeHost + "/vectors/upsert")
                .addHeader("Api-Key", pineconeKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Pinecone upsert failed: " + response.body().string());
            }
        }
    }

    /**
     * 대량 파이프라인: DB 전체 → 임베딩 생성 → Pinecone 저장
     */
    public void generateEmbeddingsForAllAnime() {
        List<TmdbAnimeEntityDto> list = animeMapper.findAllForEmbedding();

        log.info("총 애니정보 수: {}", + list.size());

        int maxCount = 500; //pinecone에 저장할 데이터 갯수
        int count = 0;
        int successCount = 0;
        int failCount = 0;


        for (TmdbAnimeEntityDto anime : list) {
            if (count >= maxCount) {
                System.out.println("최대 갯수 도달 " + maxCount + " items!");
                break;
            }

            // 1. 임베딩 개선용 데이터 전처리
            String overview = anime.getOverview();
            if (overview == null || overview.isBlank()) {
                continue; // 텍스트 정보 부족 → 임베딩 제외
            }

            count++;

            try {
                //2. 유효한 데이터만 처리
                String title = anime.getTitle();

                // 장르 불러오기
                List<String> genres = animeMapper.findGenresById(anime.getId());
                String genreText = genres.isEmpty() ? "Unknown" : String.join(", ", genres);

                // popularity + vote 정보도 포함
                StringBuilder popularitySb = new StringBuilder("Popularity: ").append(anime.getPopularity())
                        .append(", Rating: ").append(anime.getVoteAverage())
                        .append(", Votes: ").append(anime.getVoteCount());


                // passage: 프리픽스 사용
                StringBuilder sb = new StringBuilder("passage : ").append(title)
                        .append("\n OverView : ").append(overview)
                        .append("\n Genres : ").append(genreText)
                        .append("\n ").append(popularitySb);


                String text = sb.toString();

                if (text.length() > 512) {
                    text = text.substring(0, 512);
                }

                List<Double> embedding = createEmbedding(text);
                saveVectorToPinecone(anime.getId(), embedding, anime.getTitle(), genreText, anime.getPopularity()
                );

                successCount++;
                System.out.println(" [" + count + "/" + maxCount + "] 저장: " + anime.getTitle());

                // 큰 모델이라 조금 더 기다림
                Thread.sleep(3000);

            } catch (Exception e) {
                failCount++;
                System.err.println(" [" + count + "/" + maxCount + "] Error: " + e.getMessage());
            }
        }

        log.info("========== Summary ==========");
        log.info("성공: {}", successCount);
        log.info("실패: {}", failCount);
        log.info("총 처리 갯수: {}", count);


    }

    /**
     * Pinecone 에서 유사도 조회
     */
    public List<RecommendedResponseDto.RecommendedGoods> querySimilarAnime(List<Double> vector, int topK) throws IOException {

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("topK", topK);
        requestBody.add("vector", gson.toJsonTree(vector));

        RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://" + pineconeHost + "/query")
                .addHeader("Api-Key", pineconeKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Pinecone query failed: " + response.body().string());
            }

            JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
            JsonArray matches = json.getAsJsonArray("matches");

            List<RecommendedResponseDto.RecommendedGoods> recommandAnimeDtoList = new ArrayList<>();

            for (int i = 0; i < matches.size(); i++) {

                String animeId = matches.get(i).getAsJsonObject().get("id").getAsString();
                String score = matches.get(i).getAsJsonObject().get("score").getAsString();

                RecommendedResponseDto.RecommendedGoods recommendDto = RecommendedResponseDto.RecommendedGoods.builder()
                        .animeId(Long.parseLong(animeId))
                        .matchRate(
                                (double) Math.round(Double.parseDouble(score) * 100 * 100) / 100.0
                                )
                        .build();

                recommandAnimeDtoList.add(recommendDto);
            }
            return recommandAnimeDtoList;
        }
    }

}