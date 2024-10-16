package com.lotteon.controller.controller;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/prod")
@RequiredArgsConstructor
@Log4j2
public class AdminProdController {
    @GetMapping("/products")
    public String products(Model model) {

        return "pages/admin/product/list";
    }

    @GetMapping("/product/post")
    public String post(Model model) {
        return "pages/admin/product/register";
    }

    @GetMapping("/category")
    public String cate(Model model) {
        return "pages/admin/product/category";
    }

}
