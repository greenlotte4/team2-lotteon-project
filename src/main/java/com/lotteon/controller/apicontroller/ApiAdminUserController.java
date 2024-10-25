package com.lotteon.controller.apicontroller;

import com.lotteon.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
     날짜 : 2024/10/25 (금)
     이름 : 김민희
     내용 : 관리자 회원목록 수정 API Controller 생성
*/


@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Log4j2
public class ApiAdminUserController {

    private final AuthService authService;

    // 1. 관리자 회원목록 선택삭제
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteCust(@RequestBody List<Long> deleteCustIds){

        Boolean success = authService.deleteCustsById(deleteCustIds); // 담을 물건 준비
        log.info(success);

        Map<String, Object> response = new HashMap<>(); //택배 상자 준비
        response.put("success", success); // 물건 담기

        return ResponseEntity.ok(response); // 배송 ㄱㄱ
    }

}
