package com.lotteon.controller.controller;

import com.lotteon.dto.responseDto.GetBannerDTO;
import com.lotteon.dto.responseDto.GetCategoryDto;
import com.lotteon.service.category.CategoryProductService;
import com.lotteon.service.config.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CategoryProductService categoryProductService;
    private final BannerService bannerService;

    @GetMapping(value = {"/","/index"})
    public String main(Model model, @RequestParam(value = "birth", defaultValue = "false") Boolean birth, @RequestParam(value = "memState", defaultValue = "none") String memState) {
        Object category1 = categoryProductService.findCategory();
        System.out.println(category1);
        List<GetBannerDTO> banners = bannerService.selectUsingBannerAt(2);
        model.addAttribute("isMainPage", true);
        model.addAttribute("sliderb", banners);
        model.addAttribute("category1", category1);
        model.addAttribute("birth", birth);
        model.addAttribute("memState", memState);
        return "index";
    }
}
