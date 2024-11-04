package com.lotteon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RELATED_SEARCH_KEY_PREFIX = "related_search:";

    // 연관 검색어 저장
    public void saveRelatedSearch(String keyword, List<String> relatedSearches) {
        // 키 설정
        String key = RELATED_SEARCH_KEY_PREFIX + keyword.toLowerCase();
        // Redis에 저장
        redisTemplate.opsForList().rightPushAll(key, relatedSearches);
        // 만료 시간 설정 (예: 1시간)
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    // 연관 검색어 조회
    public List<String> getRelatedSearch(String keyword) {
        String key = RELATED_SEARCH_KEY_PREFIX + keyword.toLowerCase();
        return redisTemplate.opsForList().range(key, 0, -1); // 전체 조회
    }
    public List<GetLiveTopSearchDto> getTopSearches() {
        Set<ZSetOperations.TypedTuple<String>> topSearches = redisTemplate.opsForZSet()
                .reverseRangeWithScores("search_count", 0, 9);
        if (topSearches != null) {
            List<GetLiveTopSearchDto> listResults = topSearches.stream()
                    .map(tuple -> new GetLiveTopSearchDto(tuple.getValue(), tuple.getScore())) // YourType에 검색어와 카운트를 담기
                    .collect(Collectors.toList());
            return listResults;
        }
        return Collections.emptyList();
    }

    @Scheduled(fixedRate = 3600000)
    public void updateTopSearches() {
        Set<ZSetOperations.TypedTuple<String>> topSearches = redisTemplate.opsForZSet()
                .reverseRangeWithScores("search_count", 0, 9);

        if (topSearches != null && !topSearches.isEmpty()) {
            for (ZSetOperations.TypedTuple<String> tuple : topSearches) {
                String searchKeyword = tuple.getValue();
                double newScore = 1;

                assert searchKeyword != null;
                redisTemplate.opsForZSet().add("search_count", searchKeyword, newScore);
            }

            redisTemplate.expire("search_count", 2, TimeUnit.HOURS);
        }
    }
}