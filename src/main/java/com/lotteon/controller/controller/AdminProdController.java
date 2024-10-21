package com.lotteon.controller.controller;

import com.lotteon.dto.responseDto.GetCategoryDto;
import com.lotteon.dto.responseDto.GetProdCateDTO;
import com.lotteon.dto.responseDto.TestResponseDto;
import com.lotteon.service.category.CategoryProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/prod")
@RequiredArgsConstructor
@Log4j2
public class AdminProdController {
    private final CategoryProductService categoryProductService;

    private String getSideValue() {
        return "product";  // 실제 config 값을 여기에 설정합니다.
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("config", getSideValue());
        return "pages/admin/product/list";
    }

    @GetMapping("/product/post")
    public String post(Model model) {
        model.addAttribute("config", getSideValue());
        List<GetProdCateDTO> prodCate = categoryProductService.findCateAll();
        log.info("333333333333333333333333"+prodCate);
        model.addAttribute("prodCate", prodCate);
        return "pages/admin/product/register";
    }

    @GetMapping("/category")
    public String cate(Model model) {
        List<GetCategoryDto> cate1 = categoryProductService.findCategory();
        model.addAttribute("cate1", cate1);
        model.addAttribute("config", getSideValue());
        return "pages/admin/product/category";
    }

}
