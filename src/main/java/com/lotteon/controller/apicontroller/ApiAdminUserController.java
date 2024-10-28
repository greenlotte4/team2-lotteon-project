package com.lotteon.controller.apicontroller;

import com.lotteon.dto.responseDto.GetAdminUserDTO;
import com.lotteon.entity.member.Member;
import com.lotteon.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
     날짜 : 2024/10/25 (금)
     이름 : 김민희
     내용 : 관리자 회원목록 수정 API Controller 생성
     
     수정이력
      - 2025/10/27 (일) 김민희 - 회원수정 팝업호출 기능 메서드 추가
*/

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Log4j2
public class ApiAdminUserController {

    private final AuthService authService;

    // 1. 관리자 회원수정 정보조회 (+팝업호출)
    @GetMapping("/user/{id}")
    public ResponseEntity<?> popCust(@PathVariable("id") Long id) {
        log.info("id: "+id+"에 해당하는 회원 정보");
        GetAdminUserDTO custPop = authService.popCust(id);

        return ResponseEntity.ok().body(custPop);
    }

    // 2. 관리자 회원 수정
    @PutMapping("/user/{id}")
    public ResponseEntity<GetAdminUserDTO> updateCust(
                                @PathVariable("id") Long id,
                                @RequestBody GetAdminUserDTO getAdminUserDTO) {



        GetAdminUserDTO updatedCust = authService.updateCust(id,getAdminUserDTO);
        log.info("minhee : "+id);
        log.info("getAdminUserDTO : "+ getAdminUserDTO);
        return new ResponseEntity<>(updatedCust, HttpStatus.OK);
    }


    // 2. 관리자 회원목록 선택삭제
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteCust(@RequestBody List<Long> deleteCustIds){

        Boolean success = authService.deleteCustsById(deleteCustIds); // 담을 물건 준비
        log.info(success);

        Map<String, Object> response = new HashMap<>(); //택배 상자 준비
        response.put("success", success); // 물건 담기

        return ResponseEntity.ok(response); // 배송 ㄱㄱ
    }

}
