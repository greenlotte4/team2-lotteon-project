package com.lotteon.controller.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin/user")
@Controller
@RequiredArgsConstructor
@Log4j2
public class AdminUserController {



    private String getSideValue() {
        return "user";  // 실제 config 값을 여기에 설정합니다.
    }

    // 1. 관리자 회원목록
    @GetMapping("/user")
    public String user(Model model) {
        model.addAttribute("config", getSideValue());
        return "pages/admin/user/user";
    }

    // 2. 관리자 포인트관리 
    @GetMapping("/point")
    public String point(Model model) {
        model.addAttribute("config", getSideValue());
        return "pages/admin/user/point";
    }
}
