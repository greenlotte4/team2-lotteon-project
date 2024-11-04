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
}