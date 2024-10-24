package com.lotteon.controller.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/policy")
@RequiredArgsConstructor
@Log4j2
public class PolicyController {
    @GetMapping("/customer")
    public String customer(Model model) {
        return "pages/policy/customer";
    }

    @GetMapping("/seller")
    public String seller(Model model) {
        return "pages/policy/seller";
    }

    @GetMapping("/finance")
    public String finance(Model model) {
        return "pages/policy/finance";
    }

    @GetMapping("/location")
    public String location(Model model) {
        return "pages/policy/location";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        return "pages/policy/privacy";
    }
}
