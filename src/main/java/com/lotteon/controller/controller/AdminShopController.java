package com.lotteon.controller.controller;

import com.lotteon.dto.responseDto.GetShopsDto;
import com.lotteon.service.member.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/shop")
@RequiredArgsConstructor
@Log4j2
public class AdminShopController {
    private final SellerService sellerService;

    @ModelAttribute
    public void pageIndex(Model model) {
        model.addAttribute("config",getSideValue());
    }
    private String getSideValue() {
        return "shop";  // 실제 config 값을 여기에 설정합니다.
    }

    @GetMapping("/income")
    public String income(Model model) {
        model.addAttribute("active","income");
        return "pages/admin/shop/income";
    }

    @GetMapping("/shop")
    public String shop(
            Model model,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "searchType",defaultValue = "0") String searchType,
            @RequestParam(value = "keyword",defaultValue = "0") String keyword
       ) {
        model.addAttribute("active","shop");
        Page<GetShopsDto> shops;
        if(searchType.equals("0")){
            shops = sellerService.findAll(page);
        } else {
            shops = sellerService.findAllBySearch(page,searchType,keyword);
        }

        model.addAttribute("shops",shops);
        model.addAttribute("page",page);
        model.addAttribute("totalPages",shops.getTotalPages());
        model.addAttribute("searchType",searchType);
        model.addAttribute("keyword",keyword);
        return "pages/admin/shop/shop";
    }
}
