package com.lotteon.controller.apicontroller;

import com.lotteon.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class ApiSearchController {


    private final SearchService searchService;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/related-search")
    public ResponseEntity<?> getRelatedSearch(@RequestParam String keyword) {
        HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();
        Map<String, Integer> relatedSearches = hashOps.entries(keyword);
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(relatedSearches.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // 정렬된 엔트리를 새로운 LinkedHashMap으로 변환 (순서 유지)
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return ResponseEntity.ok(sortedMap.keySet());
    }
}