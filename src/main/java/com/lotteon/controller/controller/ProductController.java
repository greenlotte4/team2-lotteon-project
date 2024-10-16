package com.lotteon.controller.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/prod")
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    @GetMapping("/products")
    public String products(Model model) {
        return "pages/product/list";
    }

    @GetMapping("/product")
    public String product(Model model) {
        return "pages/product/view";
    }


    @GetMapping("/products/search")
    public String search(Model model) {
        return "pages/product/search";
    }

}
