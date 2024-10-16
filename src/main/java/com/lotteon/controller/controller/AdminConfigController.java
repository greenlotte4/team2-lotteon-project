package com.lotteon.controller.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/config")
@RequiredArgsConstructor
@Log4j2
public class AdminConfigController {
    @GetMapping("/index")
    public String index(Model model) {
        return "pages/admin/index";
    }
    @GetMapping("/basics")
    public String basic(Model model) {
        return "pages/admin/config/basic";
    }
    @GetMapping("/banners")
    public String banner(Model model) {
        return "pages/admin/config/banner";
    }
    @GetMapping("/terms")
    public String terms(Model model) {
        return "pages/admin/config/term";
    }
    @GetMapping("/versions")
    public String version(Model model) {
        return "pages/admin/config/version";
    }


}
