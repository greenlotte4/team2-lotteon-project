package com.lotteon.controller.apicontroller;

import com.lotteon.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/*
* 이름 : 이상훈
* 날짜 : 2024-10-27
* 작업내용 : 방문자수 레디스인메모리 활용해서 구하기
* */
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ApiVisitorController {
    private final VisitorService visitorService;

    @PostMapping("/track-visitor")
    public ResponseEntity<String> trackVisitor(HttpServletRequest request) {
        String key = "visitor:count:" + LocalDate.now();
        String ipAddress = request.getRemoteAddr();
        visitorService.incrementVisitorCount(key,ipAddress);

        return ResponseEntity.ok("Visitor count incremented.");
    }

    // 현재 방문자 수 조회
    @GetMapping("/visitor-count")
    public ResponseEntity<?> getVisitorCount() {
        String key = "visitor:count:" + LocalDate.now();
        Map<String,Object> map = new HashMap<>();

        Long count = visitorService.getVisitorCount(key);
        Long weekCount = visitorService.getWeekVisitorCount();

        return ResponseEntity.ok(map);
    }
}


