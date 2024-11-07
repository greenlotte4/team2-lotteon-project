package com.lotteon.controller.apicontroller;

import java.util.HashMap;
import jakarta.servlet.http.HttpSession; // javax 대신 jakarta 패키지 사용
import java.util.Map;
import com.lotteon.dto.requestDto.PatchMyInfoDTO;
import com.lotteon.service.member.CustomerService;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
@Log4j2
public class ApiMyUserController {

    private final CustomerService customerService;

    // 나의 설정 정보 수정
    @PatchMapping("/info/{type}")
    public ResponseEntity<?> modifyInfo(@PathVariable("type") String type,
                                        @RequestBody PatchMyInfoDTO patchMyInfoDTO,
                                        HttpSession session) {

        Boolean success = customerService.modifyInfo(type, patchMyInfoDTO);

        // 특정 조건에 따라 세션을 무효화합니다. 예: type이 "password"일 때 세션 무효화
        if ("password".equals(type)) { // 비밀번호가 변경된 경우에만 세션을 무효화합니다.
            session.invalidate(); // 세션 무효화
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);

        return ResponseEntity.ok(response);
    }




}
