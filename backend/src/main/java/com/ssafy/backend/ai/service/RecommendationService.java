package com.ssafy.backend.ai.service;

import com.ssafy.backend.ai.model.RecommendedResponseDto;
import com.ssafy.backend.anime.model.AnimeMapper;
import com.ssafy.backend.anime.model.TmdbAnimeEntityDto;
import com.ssafy.backend.goods.model.GoodsMapper;
import com.ssafy.backend.user.model.User;
import com.ssafy.backend.user.model.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final UserMapper userMapper;
    private final EmbeddingService embeddingService;
    private final GoodsMapper goodsMapper;
    private final AnimeMapper animeMapper;

    // 유저 벡터 캐시 (추천 애니메이션 속도 개선을 위해 데이터 담아두기)
    private final Map<Long, CachedUserVector> userVectorCache = new ConcurrentHashMap<>();

    private record CachedUserVector(String likedKey, List<Double> vector) {
    }

    /**
     * 사용자 맞춤 추천 애니메이션
     * */
    public List<RecommendedResponseDto.RecommendedAnime> recommendAnime(Long userId) throws IOException {
        List<RecommendedResponseDto.RecommendedGoods> animeIdList=  recommend(userId);

        // animeId → matchRate 매핑
        Map<Long, Double> matchRateMap = animeIdList.stream()
                .collect(Collectors.toMap(
                        RecommendedResponseDto.RecommendedGoods::getAnimeId,
                        RecommendedResponseDto.RecommendedGoods::getMatchRate
                ));

        // 추천animeIds 뽑아내기
        List<Long> animeIds = animeIdList.stream()
                .map(RecommendedResponseDto.RecommendedGoods::getAnimeId)
                .distinct()
                .toList();

        // 추천 animeIds 로 애니메이션 관련 데이터 가져오기
        List<TmdbAnimeEntityDto> animeDtoList= animeMapper.findAnimeListByIds(animeIds);

        List<RecommendedResponseDto.RecommendedAnime> resultList =
                animeDtoList.stream()
                        .map(anime -> RecommendedResponseDto.RecommendedAnime.builder()
                                .animeId(anime.getId())
                                .animeTitle(anime.getTitle())
                                .posterUrl(anime.getPosterUrl())
                                .overview(anime.getOverview())
                                .voteAverage(anime.getVoteAverage())
                                .voteCount(anime.getVoteCount())
                                .popularity(anime.getPopularity())
                                .matchRate(matchRateMap.getOrDefault(anime.getId(), 0.0))
                                .build()
                        )
                        // 매칭률 내림차순
                        .sorted(Comparator.comparing(
                                RecommendedResponseDto.RecommendedAnime::getMatchRate
                        ).reversed())
                        // 상위 6개만
                        .limit(6)
                        .toList();

        return resultList;
    }



    /*
     * AI 추천 굿즈 + 정확도(matchRate) 계산
     * 유저 벡터와 굿즈 애니 벡터 비교
     * 코사인 유사도 기반 정확도 산출
     */
    public List<RecommendedResponseDto.RecommendedGoods> recommendGoodsWithMatch(Long userId)
            throws IOException {

        // 사용자 관심 애니메이션 기반 추천 애니메이션 ID, 유사도 조회
        List<RecommendedResponseDto.RecommendedGoods> recommendList = recommend(userId);


        // animeId → matchRate 매핑
        Map<Long, Double> matchRateMap = recommendList.stream()
                .collect(Collectors.toMap(
                        RecommendedResponseDto.RecommendedGoods::getAnimeId,
                        RecommendedResponseDto.RecommendedGoods::getMatchRate
                ));

        // 추천animeIds 뽑아내기
        List<Long> animeIds = recommendList.stream()
                .map(RecommendedResponseDto.RecommendedGoods::getAnimeId)
                .distinct()
                .toList();

        // 추천animeIds 기반 굿즈 목록 조회
        List<RecommendedResponseDto.RecommendedGoods> goodsListByRecommendIds =
                goodsMapper.selectPAWRecommendGoodsList(animeIds, userId);

        // 애니메이션별 굿즈를 미리 그룹핑
        Map<Long, List<RecommendedResponseDto.RecommendedGoods>> goodsByAnime =
                goodsListByRecommendIds.stream()
                        .map(goods -> RecommendedResponseDto.RecommendedGoods.builder()
                                .goodsId(goods.getGoodsId())
                                .sellerId(goods.getSellerId())
                                .sellerNickname(goods.getSellerNickname())
                                .wishCount(goods.getWishCount())
                                .checkWish(goods.isCheckWish())
                                .category(goods.getCategory())
                                .mainFilePath(goods.getMainFilePath())
                                .title(goods.getTitle())
                                .animeTitle(goods.getAnimeTitle())
                                .currentBidAmount(goods.getCurrentBidAmount())
                                .startPrice(goods.getStartPrice())
                                .auctionStatus(goods.getAuctionStatus())
                                .auctionEndAt(goods.getAuctionEndAt())
                                .createdAt(goods.getCreatedAt())
                                .animeId(goods.getAnimeId())
                                .matchRate(matchRateMap.getOrDefault(goods.getAnimeId(), 0.0))
                                .build()
                        )
                        .collect(Collectors.groupingBy(RecommendedResponseDto.RecommendedGoods::getAnimeId));

        // 애니메이션 유사도 순 정렬 (기준 애니 리스트)
        List<Long> sortedAnimeIds = goodsByAnime.values().stream()
                .map(list -> list.get(0)) // 같은 animeId → matchRate 동일
                .sorted(Comparator.comparingDouble(RecommendedResponseDto.RecommendedGoods::getMatchRate).reversed())
                .map(RecommendedResponseDto.RecommendedGoods::getAnimeId)
                .toList();

        // 라운드 로빈 + 랜덤 추출
        int targetSize = 10;
        List<RecommendedResponseDto.RecommendedGoods> resultList = new ArrayList<>();

        // 각 애니 굿즈를 랜덤 셔플
        goodsByAnime.values().forEach(Collections::shuffle);

        int index = 0;
        while (resultList.size() < targetSize) {
            boolean added = false;

            for (Long animeId : sortedAnimeIds) {
                List<RecommendedResponseDto.RecommendedGoods> goodsList = goodsByAnime.get(animeId);

                if (goodsList.size() > index) {
                    resultList.add(goodsList.get(index));
                    added = true;

                    if (resultList.size() == targetSize) break;
                }
            }

            if (!added) break; // 더 뽑을 게 없으면 종료
            index++;
        }

        return resultList;
    }


    /*
     * AI 기반 애니메이션 추천
     * 유저가 선택한 선호 애니 1~3번을 기반으로
     * 가중 평균 임베딩을 만든 뒤 Pinecone에서 유사 애니를 조회
     */
    private List<RecommendedResponseDto.RecommendedGoods> recommend(Long userId) throws IOException {

        User user = userMapper.findById(userId);
        List<Double> userVectorList = getUserVector(user);

        if (userVectorList.isEmpty()) {
            return List.of();
        }

        // Pinecone에서 유사 애니 조회
        List<RecommendedResponseDto.RecommendedGoods> recommendAnimeList =
                embeddingService.querySimilarAnime(userVectorList, 7);

        // 이미 관심 애니로 등록된 애니는 제외
        Set<Long> likedIds = new java.util.HashSet<>();
        if (user.getLikedAnimeId1() != null) likedIds.add(user.getLikedAnimeId1());
        if (user.getLikedAnimeId2() != null) likedIds.add(user.getLikedAnimeId2());
        if (user.getLikedAnimeId3() != null) likedIds.add(user.getLikedAnimeId3());

        List<RecommendedResponseDto.RecommendedGoods> excludeRecommendList = recommendAnimeList.stream()
                .filter(dto
                        -> !likedIds.contains(dto.getAnimeId()))
                .limit(5)
                .toList();

        return excludeRecommendList;
    }

    /**
     * 유저 벡터 캐싱
     * */
    private List<Double> getUserVector(User user) throws IOException {

        String likedKey = buildLikedKey(user);
        CachedUserVector cached = userVectorCache.get(user.getId());

        // ✅ 캐시 히트 (관심 애니 동일)
        if (cached != null && likedKey.equals(cached.likedKey)) {
            return cached.vector;
        }

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

        if (vectors.isEmpty()) {
            return List.of();
        }

        List<Double> userVector = embeddingService.weightedUserEmbedding(vectors, weights);

        userVectorCache.put(
                user.getId(),
                new CachedUserVector(likedKey, userVector)
        );

        return userVector;
    }

    private String buildLikedKey(User user) {
        return user.getLikedAnimeId1() + "-"
                + user.getLikedAnimeId2() + "-"
                + user.getLikedAnimeId3();
    }

}
