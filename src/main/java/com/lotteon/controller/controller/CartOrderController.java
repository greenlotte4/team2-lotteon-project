package com.lotteon.controller.controller;

import com.lotteon.dto.requestDto.PostCartDto;
import com.lotteon.service.product.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/prod")
@RequiredArgsConstructor
@Log4j2
public class CartOrderController {

    private final CartService cartService;

    @GetMapping("/cart")
    public String join(Model model) {
        return "pages/product/cart";
    }

    @PostMapping("/cart")
    public void cart(PostCartDto postCartDto){

        ResponseEntity result = cartService.insertCart(postCartDto);

    }

    @GetMapping("/cart/direct")
    public String cartDirect(Model model) {
        return "redirect:/prod/order";
    }

    @GetMapping("/order")
    public String order(Model model) {
        return "pages/product/order";
    }

    @GetMapping("/order/complete")
    public String orderComplete(Model model) {
        return "pages/product/complete";
    }
}
