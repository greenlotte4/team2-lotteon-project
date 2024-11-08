package com.lotteon.service;

import com.lotteon.entity.config.VisitorCount;
import com.lotteon.repository.config.VisitorCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VisitorService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final VisitorCountRepository visitorCountRepository;

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
        setKeyExpiration(key, 86400);
        setKeyExpiration(ipKey, 86400);
    }

    // 방문자 수 조회 메서드
    public Long getVisitorCount(String key) {
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.parseLong(String.valueOf(count)) : 0L;
    }

    @Scheduled(cron = "20 59 23 * * ?")
    public void saveVisitorCount() {
        LocalDate today = LocalDate.now();
        String todayKey = "visitor:count:" + today;
        Long count = getVisitorCount(todayKey);
        VisitorCount visitorCount = VisitorCount.builder()
                .visitorCnt(count)
                .visitorDate(today)
                .build();
        visitorCountRepository.save(visitorCount);
    }

    public Long findVisitorCount(LocalDate today) {

        VisitorCount visitorCount = visitorCountRepository.findByVisitorDate(today);
        if(visitorCount == null) {
            return 0L;
        }
        return visitorCount.getVisitorCnt();
    }

    public Long findVisitorCountOfWeek(LocalDate today) {
        long totalVisitorCount = 0;

        // 오늘 날짜로부터 일주일 전의 날짜 계산
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            // 각 날짜에 대한 방문자 수 가져오기
            VisitorCount visitorCount = visitorCountRepository.findByVisitorDate(date);
            if (visitorCount != null) {
                totalVisitorCount += visitorCount.getVisitorCnt(); // 방문자 수 합산
            }
        }
        return totalVisitorCount;
    }

//    public Long getWeekVisitorCount() {
//        long weekCount = 0;
//        for (int i = 0; i < 7; i++) {
//            String key = "visitor:count:" + LocalDate.now().minusDays(i);
//            weekCount += getVisitorCount(key);
//        }
//        return weekCount;
//    }

    public void setKeyExpiration(String key, long ttl) {
        redisTemplate.expire(key, Duration.ofSeconds(ttl));
    }

}
