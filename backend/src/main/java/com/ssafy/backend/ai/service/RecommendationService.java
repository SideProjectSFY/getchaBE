package com.ssafy.backend.ai.service;

import com.ssafy.backend.anime.model.AnimeMapper;
import com.ssafy.backend.anime.model.TmdbAnimeEntityDto;
import com.ssafy.backend.goods.model.Goods;
import com.ssafy.backend.goods.model.GoodsMapper;
import com.ssafy.backend.user.model.User;
import com.ssafy.backend.user.model.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserMapper userMapper;
    private final EmbeddingService embeddingService;
    private final AnimeMapper animeMapper;
    private final GoodsMapper goodsMapper;

    public List<TmdbAnimeEntityDto> recommend(Long userId) throws IOException {

        User user = userMapper.findById(userId);

        List<List<Double>> vectors = new ArrayList<>();
        List<Double> weights = new ArrayList<>();

        if (user.getLikedAnimeId1() != null) {
            vectors.add(embeddingService.getAnimeEmbedding(user.getLikedAnimeId1()));
            weights.add(0.5); //대표 선호
        }

        if (user.getLikedAnimeId2() != null) {
            vectors.add(embeddingService.getAnimeEmbedding(user.getLikedAnimeId2()));
            weights.add(0.3);
        }

        if (user.getLikedAnimeId3() != null) {
            vectors.add(embeddingService.getAnimeEmbedding(user.getLikedAnimeId3()));
            weights.add(0.2);
        }

        List<Double> userVector = embeddingService.weightedUserEmbedding(vectors, weights);

        List<String> ids = embeddingService.querySimilarAnime(userVector, 7);

        // 유저가 이미 좋아하는 애니 ID Set 제외용
        Set<Long> likedIds = Set.of(
                user.getLikedAnimeId1(),
                user.getLikedAnimeId2(),
                user.getLikedAnimeId3()
        );

        List<Long> animeIds = ids.stream()
                .map(Long::valueOf)
                .filter(id -> !likedIds.contains(id))
                .limit(5)
                .toList();

        return animeMapper.findByIds(animeIds);
    }

    public List<Goods> recommendGoods(Long userId) throws IOException {

        // 1. AI 기반 추천
        List<TmdbAnimeEntityDto> animeList = recommend(userId);
        List<Long> animeIds = animeList.stream()
                .map(TmdbAnimeEntityDto::getId)
                .toList();

        List<Goods> result = new ArrayList<>();

        if (!animeIds.isEmpty()) {
            List<Goods> aiGoods =
                    goodsMapper.selectOngoingGoodsByAnimeIdsExcludeSellerAndWish(
                            animeIds, userId, userId
                    );

            result.addAll(
                    aiGoods.stream()
                            .sorted((g1, g2) -> {

                                // 기본 점수: 애니 추천 순위
                                double score1 = (animeIds.size() - animeIds.indexOf(g1.getAnimeId()));
                                double score2 = (animeIds.size() - animeIds.indexOf(g2.getAnimeId()));

                                // 마감 임박 가중치
                                score1 += urgencyBoost(g1.getAuctionEndAt());
                                score2 += urgencyBoost(g2.getAuctionEndAt());

                                return Double.compare(score2, score1);
                            })
                            .limit(5)
                            .toList()
            );
        }

        // 2. 부족하면 인기 굿즈로 채우기
        if (result.size() < 5) {
            int need = 5 - result.size();

            List<Long> alreadyAddedIds = result.stream()
                    .map(Goods::getId)
                    .toList();

            List<Goods> fallback =
                    goodsMapper.selectPopularOngoingGoodsExcludeSellerAndWish(
                            userId,
                            userId,
                            need
                    );

            // 혹시 중복 방지
            fallback.stream()
                    .filter(g -> !alreadyAddedIds.contains(g.getId()))
                    .forEach(result::add);
        }

        return result;
    }

    // 경매 종료 시간이 가까울수록 가중치 부여
    private double urgencyBoost(LocalDateTime auctionEndAt) {

        if (auctionEndAt == null) return 0.0;

        long minutesLeft =
                java.time.Duration.between(
                        LocalDateTime.now(),
                        auctionEndAt
                ).toMinutes();

        if (minutesLeft <= 60) {
            return 0.3;
        } else if (minutesLeft <= 360) { // 6시간
            return 0.2;
        } else if (minutesLeft <= 1440) { // 24시간
            return 0.1;
        }
        return 0.0;
    }

}

