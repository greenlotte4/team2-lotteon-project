package com.lotteon.controller.apicontroller;

import com.lotteon.dto.requestDto.PostFindIdDto;
import com.lotteon.service.AuthService;
import com.lotteon.service.EmailService;
import com.lotteon.service.member.CustomerService;
import com.lotteon.service.member.MemberService;
import com.lotteon.service.member.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
*   이름 : 이상훈
*   날짜 : 202-10-28
*   작업내용 : 셀러 / 커스터머 아이디찾기 기능 구현
* */

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class ApiAuthController {

    private final EmailService emailService;
    private final AuthService authService;
    private final CustomerService customerService;
    private final SellerService sellerService;
    private final MemberService memberService;

    @PostMapping("/send/{type}")
    public ResponseEntity<?> sendEmail(@PathVariable String type, @RequestBody PostFindIdDto dto) {
        if(type.equals("customer")){
            String result = authService.matchingCheckByNameAndEmail(dto.getName(),dto.getEmail());
            if(result.equals("NF")){
                return ResponseEntity.ok("NF");
            }
            String randomCode = String.format("%06d", (int)(Math.random() * 1000000));
            emailService.sendEmail(dto.getEmail(),dto.getName() +"님 (LotteOn)아이디찾기 인증코드입니다.",randomCode);
            return ResponseEntity.ok(randomCode);
        } else if(type.equals("seller")) {
            String result = authService.matchingCheckByBusinessAndEmail(dto.getName(),dto.getEmail());
            if(result.equals("NF")){
                return ResponseEntity.ok("NF");
            }
            String randomCode = String.format("%06d", (int)(Math.random() * 1000000));
            emailService.sendEmail(dto.getEmail(),result +"님 (LotteOn)아이디찾기 인증코드입니다.",randomCode);
            return ResponseEntity.ok(randomCode);
        } else if(type.equals("customerpwd")){
            String result = authService.matchingCheckByUidAndEmail(dto.getName(),dto.getEmail());
            if(result.equals("NF")){
                return ResponseEntity.ok("NF");
            }
            String randomCode = String.format("%06d", (int)(Math.random() * 1000000));
            emailService.sendEmail(dto.getEmail(),dto.getName() +"님 (LotteOn)아이디찾기 인증코드입니다.",randomCode);
            return ResponseEntity.ok(randomCode);
        } else {
            String result = authService.matchingCheckByBusinessAndEmailAndUid(dto.getName(),dto.getEmail(),dto.getUid());
            if(result.equals("NF")){
                return ResponseEntity.ok("NF");
            }
            String randomCode = String.format("%06d", (int)(Math.random() * 1000000));
            emailService.sendEmail(dto.getEmail(),result +"님 (LotteOn)아이디찾기 인증코드입니다.",randomCode);
            return ResponseEntity.ok(randomCode);
        }

    }

    @PostMapping("/email/{type}")
    public ResponseEntity<?> getMemUid(@PathVariable String type, @RequestBody PostFindIdDto dto) {
        String memUid;
        if(type.equals("customer")){
            memUid = customerService.findByNameAndEmail(dto);
        } else {
            memUid = sellerService.findByCodeAndEmail(dto);
        }
        return ResponseEntity.ok(memUid);
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePwd(@RequestBody PostFindIdDto dto){
        memberService.updatePwd(dto.getPwd(),dto.getUid());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/check/uid")
    public ResponseEntity<?> checkUid(@RequestParam String uid){
        Map<String,String> map = memberService.findByUid(uid);

        return ResponseEntity.ok().body(map);
    }


    @PostMapping("/check/email")
    public ResponseEntity<?> checkEmail(@RequestParam String email){
        Map<String,String> map = memberService.findByEmail(email);

        return ResponseEntity.ok().body(map);
    }

    @PostMapping("/check/company")
    public ResponseEntity<?> checkCompany(@RequestParam String company){
        Map<String,String> map = memberService.findByCompany(company);

        return ResponseEntity.ok().body(map);
    }



}
