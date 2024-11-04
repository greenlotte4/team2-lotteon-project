package com.lotteon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VisitorService {
    private final RedisTemplate<String, Object> redisTemplate;

    // 방문자 수 증가 메서드
    public void incrementVisitorCount(String key, String ipAddress) {
        String ipKey = "visitor:ips:" + LocalDate.now();
        BoundSetOperations<String, Object> setOps = redisTemplate.boundSetOps(ipKey);
        if (Boolean.TRUE.equals(setOps.isMember(ipAddress))) {
            System.out.println("같은 아이피접속");
            return;
        }
        System.out.println("다른아이피 접속");
        setOps.add(ipAddress);

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.increment(key);
        setKeyExpiration(key, 86400 * 7);
        setKeyExpiration(ipKey, 86400);
    }

    // 방문자 수 조회 메서드
    public Long getVisitorCount(String key) {
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.parseLong(String.valueOf(count)) : 0L;
    }

    public Long getWeekVisitorCount() {
        long weekCount = 0;
        for (int i = 0; i < 7; i++) {
            String key = "visitor:count:" + LocalDate.now().minusDays(i);
            weekCount += getVisitorCount(key);
        }
        return weekCount;
    }

    public void setKeyExpiration(String key, long ttl) {
        redisTemplate.expire(key, Duration.ofSeconds(ttl));
    }

}
