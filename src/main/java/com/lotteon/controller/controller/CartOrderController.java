package com.lotteon.controller.controller;

import com.lotteon.dto.requestDto.PostCartDto;
import com.lotteon.dto.requestDto.PostCartSaveDto;
import com.lotteon.dto.responseDto.GetCartDto;
import com.lotteon.dto.responseDto.GetCategoryDto;
import com.lotteon.dto.responseDto.GetOrderDto;
import com.lotteon.dto.responseDto.cartOrder.GetOrderUserDto;
import com.lotteon.entity.product.Cart;
import com.lotteon.service.category.CategoryProductService;
import com.lotteon.service.member.CustomerService;
import com.lotteon.service.product.CartService;
import com.lotteon.service.product.OrderService;
import com.lotteon.service.product.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/prod")
@RequiredArgsConstructor
@Log4j2
public class CartOrderController {

    private final CartService cartService;
    private final CategoryProductService categoryProductService;
    private final OrderService orderService;
    private final CustomerService customerService;

    @GetMapping("/cart")
    public String join(Model model, HttpSession session) {

        List<GetCartDto> cartItems = cartService.selectCart(session);
        log.info("카트 아이템 데이터 구조 좀 보자 "+cartItems);
        model.addAttribute("cartItems", cartItems);

        List<GetCategoryDto> category1 = categoryProductService.findCategory();

        model.addAttribute("category1", category1);
        return "pages/product/cart";
    }

    @PostMapping("/cart")
    public String cart(PostCartDto postCartDto, Model model, HttpSession session) {

        ResponseEntity result = cartService.insertCart(postCartDto, session);
        log.info(result.getBody());

        if(result.getStatusCode() == HttpStatus.UNAUTHORIZED){
            return "redirect:/prod/cart";
        }


        Cart cart = (Cart) result.getBody();
        ResponseEntity result2 = cartService.insertCartItem(postCartDto,cart);

        if(result2.getBody().equals("insert")){
            model.addAttribute(true);
            return "redirect:/prod/cart";
        }else {
            model.addAttribute(false);
            return "redirect:/prod/product";
        }

    }

    @GetMapping("/cart/direct")
    public String cartDirect(Model model) {
        return "redirect:/prod/order";
    }

    @GetMapping("/order")
    public String order(Model model ,HttpSession session ) {
        List<GetCategoryDto> category1 = categoryProductService.findCategory();
        model.addAttribute("category1", category1);

        List<PostCartSaveDto> selectedProducts = (List<PostCartSaveDto>) session.getAttribute("selectedProducts");
        log.info("주문서에서 선택한 상품 "+selectedProducts.toString());

        GetOrderUserDto customer = customerService.selectedOrderCustomer();

        List<GetOrderDto> orders = orderService.selectedOrders(selectedProducts);

        model.addAttribute("orders", orders);
        model.addAttribute("customer", customer);


        return "pages/product/order";
    }

    @GetMapping("/order/complete")
    public String orderComplete(Model model) {
        List<GetCategoryDto> category1 = categoryProductService.findCategory();

        model.addAttribute("category1", category1);
        return "pages/product/complete";
    }
}
