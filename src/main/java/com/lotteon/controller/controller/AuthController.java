package com.lotteon.controller.controller;


import com.lotteon.dto.requestDto.PostCustSignupDTO;
import com.lotteon.dto.responseDto.GetTermsResponseDto;
import com.lotteon.entity.term.Terms;
import com.lotteon.service.member.CustomerService;
import com.lotteon.service.member.MemberService;
import com.lotteon.service.member.SellerService;
import com.lotteon.service.term.TermsService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;
    private final MemberService memberService;
    private final SellerService sellerService;
    private final TermsService termsService;

    @GetMapping("/login/view")
    public String login() {
        return "pages/auth/login";
    }

    @GetMapping("/join")
    public String join() {
        return "pages/auth/join";
    }
    
    // 1. 이용약관 Terms
    @GetMapping("/signup")
    public String signUp(@RequestParam String termsType, Model model) {
        List<GetTermsResponseDto> getterms = termsService.selectTerms(termsType);
        log.info(getterms);
        model.addAttribute("terms", getterms); // 뷰에서 보이는 거
        model.addAttribute("termsType", termsType); // 뷰에서 보이는 거
        return "pages/auth/signup";
    }

    // 2. 회원가입 (일반회원 정보입력) | optional : 선택약관 동의 여부
    // 체크박스 선택 여부에 따라 true=1, false=0
    @GetMapping("/customer/{optional}")
    public String customer(@PathVariable String optional, Model model) {
        log.info("체크박스 선택 여부에 따라 true=1, false=0 >>>>>>>" + optional);
        return "pages/auth/customer";
    }

    // 2. 회원가입 (일반회원 정보입력)
    @PostMapping("/customer")
    public String customer(
             PostCustSignupDTO postCustSignupDTO) {
        log.info("Register Controller - UserDTO :"+postCustSignupDTO.toString());
        customerService.insertCustomer(postCustSignupDTO);

        return "redirect:/auth/login/view";
    }

    // 회원가입 (판매회원 정보입력)
    @GetMapping("/seller")
    public String seller(){
        return "pages/auth/seller";
    }


}