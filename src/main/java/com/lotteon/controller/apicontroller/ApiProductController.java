package com.lotteon.controller.apicontroller;

import com.lotteon.dto.requestDto.PostCartSaveDto;
import com.lotteon.service.point.CustomerCouponService;
import com.lotteon.service.product.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/prod")
@RequiredArgsConstructor
public class ApiProductController {
    private final CustomerCouponService customerCouponService;
    private final CartService cartService;

    @GetMapping("/test/coupon")
    public void toTestCouponIssue(){

        customerCouponService.useCustCoupon();
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Long> CartDelete(@RequestBody List<Long> cartItemIds ){
        Long deletedCartItem = cartService.deleteCartItem(cartItemIds);
        return ResponseEntity.ok(deletedCartItem);
    }

    @PostMapping("/cart/save")
    public ResponseEntity<Boolean> CartToOrder(@RequestBody List<PostCartSaveDto> selectedProducts, HttpSession session){
        log.info("선택한 카트 정보 "+selectedProducts.toString());
        if(selectedProducts.isEmpty()){
            return ResponseEntity.ok(false);
        }else {
            session.setAttribute("selectedProducts", selectedProducts);
            return ResponseEntity.ok(true);
        }
    }
}
