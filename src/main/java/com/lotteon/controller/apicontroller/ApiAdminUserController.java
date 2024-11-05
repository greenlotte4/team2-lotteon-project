package com.lotteon.controller.apicontroller;

import com.lotteon.dto.responseDto.GetAdminUserDTO;
import com.lotteon.entity.member.Member;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.service.AuthService;
import com.lotteon.service.member.MemberService;
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
    private final MemberRepository memberRepository;


    // 1. 관리자 회원수정 정보조회 (+팝업호출)
    @GetMapping("/user/{id}")
    public ResponseEntity<?> popCust(@PathVariable("id") Long id) {
        log.info("id: " + id + "에 해당하는 회원 정보");

        // `AuthService.popCust(id)`에서 역할에 따른 로직 분기
        Optional<Member> memberOpt = memberRepository.findByCustomer_id(id);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            String role = member.getMemRole();

            // `customer` 역할일 경우 Customer 정보 DTO 반환
            if (role.equals("customer")) {
                GetAdminUserDTO custPop = authService.popCust(id);  // 기존 방식 유지
                System.out.println(custPop.getMemRole());
                log.info("customer 정보: " + custPop);
                return ResponseEntity.ok().body(custPop);

            // `guest` 역할일 경우 새로운 DTO 반환
            } else {
                GetAdminUserDTO guestPop = authService.popGuest(id);  // 새로운 guest용 메서드
                System.out.println(guestPop.getMemRole());
                log.info("guest 정보: " + guestPop);
                return ResponseEntity.ok().body(guestPop);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID를 가진 회원 정보가 없습니다.");
        }
    }

    // 2. 관리자 회원 수정
    @PutMapping("/user/{id}")
    public ResponseEntity<GetAdminUserDTO> updateCust(
                                @PathVariable("id") Long id,
                                @RequestBody GetAdminUserDTO getAdminUserDTO) {

        log.info("minhee : "+id);
        log.info("getAdminUserDTO : "+ getAdminUserDTO);

        GetAdminUserDTO updatedCust = authService.updateCust(id,getAdminUserDTO);

        return ResponseEntity.ok().body(updatedCust);
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
