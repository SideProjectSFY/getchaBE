package com.ssafy.backend.ai.service;

import com.ssafy.backend.ai.model.RecommendedGoodsDto;
import com.ssafy.backend.anime.model.AnimeMapper;
import com.ssafy.backend.anime.model.TmdbAnimeEntityDto;
import com.ssafy.backend.goods.model.Goods;
import com.ssafy.backend.goods.model.GoodsMapper;
import com.ssafy.backend.user.model.User;
import com.ssafy.backend.user.model.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final UserMapper userMapper;
    private final EmbeddingService embeddingService;
    private final AnimeMapper animeMapper;
    private final GoodsMapper goodsMapper;

    /*
     * AI 기반 애니메이션 추천
     * 유저가 선택한 선호 애니 1~3번을 기반으로 가중 평균 임베딩을 만든 뒤 Pinecone에서 유사 애니를 조회
     */
    public List<TmdbAnimeEntityDto> recommend(Long userId) throws IOException {

        User user = userMapper.findById(userId);

        log.info("likedAnime1 = {}", user.getLikedAnimeId1());
        log.info("likedAnime2 = {}", user.getLikedAnimeId2());
        log.info("likedAnime3 = {}", user.getLikedAnimeId3());

        // 유저 선호 애니 임베딩 벡터 + 가중치
        List<List<Double>> vectors = new ArrayList<>();
        List<Double> weights = new ArrayList<>();

        if (user.getLikedAnimeId1() != null) {
            vectors.add(embeddingService.getAnimeEmbedding(user.getLikedAnimeId1()));
            weights.add(0.5); // 대표 선호
        }
        if (user.getLikedAnimeId2() != null) {
            vectors.add(embeddingService.getAnimeEmbedding(user.getLikedAnimeId2()));
            weights.add(0.3);
        }
        if (user.getLikedAnimeId3() != null) {
            vectors.add(embeddingService.getAnimeEmbedding(user.getLikedAnimeId3()));
            weights.add(0.2);
        }

        // 선호 애니가 하나도 없으면 추천 불가
        if (vectors.isEmpty()) return List.of();

        // 가중 평균 유저 벡터 생성
        List<Double> userVector =
                embeddingService.weightedUserEmbedding(vectors, weights);

        // Pinecone에서 유사 애니 조회
        List<String> ids =
                embeddingService.querySimilarAnime(userVector, 7);

        // 이미 관심 애니로 등록해 놓은 애니는 추천에서 제외
        Set<Long> likedIds = new java.util.HashSet<>();
        if (user.getLikedAnimeId1() != null) likedIds.add(user.getLikedAnimeId1());
        if (user.getLikedAnimeId2() != null) likedIds.add(user.getLikedAnimeId2());
        if (user.getLikedAnimeId3() != null) likedIds.add(user.getLikedAnimeId3());

        List<Long> animeIds = ids.stream()
                .map(Long::valueOf)
                .filter(id -> !likedIds.contains(id))
                .limit(5)
                .toList();

        return animeMapper.findByIds(animeIds);
    }

    /*
     * AI 추천 애니 기반 굿즈 조회
     * 추천 애니에 해당하는 굿즈 중 auction_status가 진행 중인 굿즈만 조회
     */
    private List<Goods> recommendAiGoods(Long userId) throws IOException {

        List<TmdbAnimeEntityDto> animeList = recommend(userId);
        List<Long> animeIds = animeList.stream()
                .map(TmdbAnimeEntityDto::getId)
                .toList();

        if (animeIds.isEmpty()) return List.of();

        return goodsMapper.selectOngoingGoodsByAnimeIdsExcludeSellerAndWish(
                animeIds, userId, userId
        );
    }

    /*
     * fallback 굿즈 조회
     * AI 추천 굿즈가 부족할 경우 인기 굿즈로 대체
     */
    private List<Goods> recommendFallbackGoods(Long userId, int limit) {
        return goodsMapper.selectPopularOngoingGoodsExcludeSellerAndWish(
                userId, userId, limit
        );
    }

    /*
     * AI 추천 굿즈 + 정확도(matchRate) 계산
     * 유저 벡터와 굿즈 애니 벡터 비교
     * 코사인 유사도 기반 정확도 산출
     */
    /*
     * AI 추천 굿즈 + 정확도(matchRate) 계산
     * 유저 벡터와 굿즈 애니 벡터 비교
     * 코사인 유사도 기반 정확도 산출
     */
    public List<RecommendedGoodsDto> recommendGoodsWithMatch(Long userId)
            throws IOException {

        User user = userMapper.findById(userId);

        // 유저 임베딩 벡터 생성 (애니 추천과 동일 로직)
        List<List<Double>> vectors = new ArrayList<>();
        List<Double> weights = new ArrayList<>();

        if (user.getLikedAnimeId1() != null) {
            vectors.add(embeddingService.getAnimeEmbedding(user.getLikedAnimeId1()));
            weights.add(0.5);
        }
        if (user.getLikedAnimeId2() != null) {
            vectors.add(embeddingService.getAnimeEmbedding(user.getLikedAnimeId2()));
            weights.add(0.3);
        }
        if (user.getLikedAnimeId3() != null) {
            vectors.add(embeddingService.getAnimeEmbedding(user.getLikedAnimeId3()));
            weights.add(0.2);
        }

        if (vectors.isEmpty()) return List.of();

        List<Double> userVector =
                embeddingService.weightedUserEmbedding(vectors, weights);

        List<RecommendedGoodsDto> result = new ArrayList<>();

        // 1. AI 기반 굿즈
        List<Goods> aiGoods = recommendAiGoods(userId);

        for (Goods goods : aiGoods) {
            try {
                List<Double> animeVector =
                        embeddingService.getAnimeEmbedding(goods.getAnimeId());

                int matchRate = (int) Math.round(
                        cosineSimilarity(userVector, animeVector) * 100
                );

                result.add(new RecommendedGoodsDto(goods, matchRate, true));
            } catch (Exception e) {
                log.warn("[AI GOODS] 임베딩 없음 → skip. animeId={}", goods.getAnimeId());
            }
        }

        // 2. fallback 굿즈
        if (result.size() < 5) {
            int need = 5 - result.size();

            List<Goods> fallbackGoods =
                    recommendFallbackGoods(userId, need);

            for (Goods goods : fallbackGoods) {
                try {
                    List<Double> animeVector =
                            embeddingService.getAnimeEmbedding(goods.getAnimeId());

                    int matchRate = (int) Math.round(
                            cosineSimilarity(userVector, animeVector) * 100
                    );

                    result.add(new RecommendedGoodsDto(goods, matchRate, false));

                } catch (Exception e) {
                    log.warn("[FALLBACK] 임베딩 없음 → skip. animeId={}", goods.getAnimeId());
                }
            }
        }

        return result;
    }

    /*
     * 코사인 유사도 계산
     */
    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += v1.get(i) * v1.get(i);
            norm2 += v2.get(i) * v2.get(i);
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /*
     * 마감 임박 가중치 (굿즈 정렬 보조 기준)
     */
    private double urgencyBoost(LocalDateTime auctionEndAt) {

        if (auctionEndAt == null) return 0.0;

        long minutesLeft =
                java.time.Duration.between(
                        LocalDateTime.now(),
                        auctionEndAt
                ).toMinutes();

        if (minutesLeft <= 60) return 0.3;
        if (minutesLeft <= 360) return 0.2;
        if (minutesLeft <= 1440) return 0.1;

        return 0.0;
    }
}

