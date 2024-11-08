package com.lotteon.controller.controller;


import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostCustSignupDTO;
import com.lotteon.dto.requestDto.PostSellerSignupDTO;
import com.lotteon.dto.responseDto.GetBannerDTO;
import com.lotteon.dto.responseDto.GetTermsResponseDto;
import com.lotteon.entity.member.Member;
import com.lotteon.service.config.BannerService;
import com.lotteon.service.member.CustomerService;
import com.lotteon.service.member.MemberService;
import com.lotteon.service.member.SellerService;
import com.lotteon.service.term.TermsService;

import lombok.extern.log4j.Log4j2;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Log4j2
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;
    private final TermsService termsService;
    private final BannerService bannerService;
    private final SellerService sellerService;

    @GetMapping("/login/view")
    public String login(Model model) {
        List<GetBannerDTO> banners = bannerService.selectUsingBannerAt(4);
        if(banners.size()>1){
            Random random = new Random();
            int randomIndex = random.nextInt(banners.size());
            model.addAttribute("banner", banners.get(randomIndex));
        }else{
            model.addAttribute("banner", banners.get(0));
        }
        return "pages/auth/login";
    }

    @GetMapping("/join")
    public String join() {
        return "pages/auth/join";
    }
    
    // 1. 이용약관 Terms (termsType = 일반 customer,판매자 seller)
    @GetMapping("/signup")
    public String signUp(@RequestParam String termsType, Model model) {
        List<GetTermsResponseDto> getterms = termsService.selectTerms(termsType);
        log.info(getterms);
        model.addAttribute("terms", getterms); // 뷰에서 보이는 거
        model.addAttribute("termsType", termsType); // customer일 때만 선택 약관 조회
        return "pages/auth/signup";
    }

    // 2-1. 회원가입 (일반회원 정보입력) | optional : 선택약관 동의 여부
    // 체크박스 선택 여부에 따라 true=1, false=0
    @GetMapping("/customer/{optional}")
    public String customer(@PathVariable int optional, Model model) {
        //Boolean isOptional = optional.orElse(false);  // 값이 없으면 기본값 false
        log.info("체크박스 선택 여부에 따라 true=1, false=0 >>>>>>> " + optional);
        model.addAttribute("isOptional", optional);

        return "pages/auth/customer";
    }

    // 2-1. 회원가입 (일반회원 정보입력)
    @PostMapping("/customer")
    public String customer(PostCustSignupDTO postCustSignupDTO) {

        log.info("일반회원 정보입력 : " + postCustSignupDTO.toString());
        postCustSignupDTO.setCustHp(postCustSignupDTO.getCustHp().replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3"));
        customerService.insertCustomer(postCustSignupDTO);

        return "redirect:/auth/login/view";
    }

    // 2-2. 회원가입 (판매회원 정보입력)
    @GetMapping("/seller")
    public String seller(){
        return "pages/auth/seller";
    }

    // 2-2. 회원가입 (판매회원 정보입력)
    @PostMapping("/seller")
    public String seller(PostSellerSignupDTO postSellerSignupDTO){
        sellerService.insertSeller(postSellerSignupDTO);
        return "redirect:/auth/login/view";
    }

    @GetMapping("/findid")
    public String findId(Model model) {

        return "pages/auth/findid";
    }
    @GetMapping("/findpwd")
    public String findPwd(Model model) {

        return "pages/auth/findpwd";
    }

    @GetMapping("/findpwd/{type}/result")
    public String findPwdCustomer(Model model, @PathVariable String type, @RequestParam String uid) {
        model.addAttribute("uid",uid);
        model.addAttribute("type",type);
        return "pages/auth/result";
    }


}


