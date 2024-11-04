package com.lotteon.controller.controller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.responseDto.GetAddressDto;
import com.lotteon.dto.responseDto.GetCustomerCouponDto;
import com.lotteon.dto.responseDto.GetMyCouponDto;
import com.lotteon.dto.responseDto.GetPointsDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrdersDto;
import com.lotteon.entity.member.Customer;
import com.lotteon.service.member.AddressService;
import com.lotteon.service.member.CustomerService;
import com.lotteon.service.point.CouponService;
import com.lotteon.service.point.CustomerCouponService;
import com.lotteon.service.point.PointService;
import com.lotteon.service.product.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
@Log4j2
public class MyController {

    private final OrderService orderService;
    private final CouponService couponService;
    private final CustomerCouponService customerCouponService;
    private final CustomerService customerService;
    private final PointService pointService;
    private final AddressService addressService;

    @ModelAttribute
    public void commonAttributes(Model model) {
        int hasCoupon = customerCouponService.findAllCntByCustomer();
        model.addAttribute("hasCoupon", hasCoupon);
        int hasPoint = customerService.findByCustomer();
        model.addAttribute("hasPoint", hasPoint);
    }

    @GetMapping(value = {"","/","/index"})
    public String index(Model model) {

        Page<ResponseOrdersDto> orders = orderService.selectedOrderList(0);


        Page<GetPointsDto> points = pointService.findAllByCustomer(0);
        if(points.isEmpty()){
            model.addAttribute("noPoint",true);
            return "pages/my/index";
        }
        model.addAttribute("orders", orders);
        model.addAttribute("points", points);
        model.addAttribute("noPoint",false);
        return "pages/my/index";
    }
    @GetMapping("/coupons")
    public String coupon(
            Model model,
            @RequestParam(name = "page",defaultValue = "0") int page
    ) {
        Page<GetMyCouponDto> coupons = customerCouponService.findAllByCustomer(page);
        if(coupons==null){
            model.addAttribute("noItem",true);
            return "pages/my/coupon";
        }
        model.addAttribute("noItem",false);
        model.addAttribute("coupons", coupons);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", coupons.getTotalPages());

        return "pages/my/coupon";
    }
    @GetMapping("/info")
    public String info(Model model) {
        return "pages/my/info";
    }

    @GetMapping("/orders")
    public String order(Model model,
                        @RequestParam(name = "page",defaultValue = "0") int page,
                        @RequestParam(name = "type",defaultValue = "0") String type,
                        @RequestParam(name = "keyword",defaultValue = "0") String keyword
    ) {
        log.info("마이페이지 오더 컨트롤러 접속 ");
        Page<ResponseOrdersDto> orders;

        if(!type.equals("0")&&!keyword.equals("0")){
            orders = orderService.findAllBySearch(page,type,keyword);
        }else {
            orders = orderService.selectedOrderList(page);
        }

        log.info("마이페이지 오더 컨트롤러 오더 잘 뽑혔는지 확인 "+orders);
        if(orders.isEmpty()){
            model.addAttribute("noItem",true);
            return "pages/my/order";
        }

        model.addAttribute("orders", orders);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("noItem",false);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        return "pages/my/order";
    }
    @GetMapping("/points")
    public String point(
            Model model,
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "type",defaultValue = "0") String type,
            @RequestParam(name = "keyword",defaultValue = "0") String keyword
            ) {
        Page<GetPointsDto> points;
        if(!type.equals("0")&&!keyword.equals("0")){
            points = pointService.findAllBySearch(page,type,keyword);
        } else {
            points = pointService.findAllByCustomer(page);
        }
        if(points.isEmpty()){
            model.addAttribute("noItem",true);
            return "pages/my/point";
        }
        model.addAttribute("noItem",false);
        model.addAttribute("points", points);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", points.getTotalPages());

        return "pages/my/point";
    }
    @GetMapping("/qnas")
    public String qna(Model model) {
        return "pages/my/qna";
    }
    @GetMapping("/reviews")
    public String review(Model model) {
        return "pages/my/review";
    }
    @GetMapping("/address")
    public String address(Model model) {
        List<GetAddressDto> addrs = addressService.findAllByCustomer();
        model.addAttribute("addrs",addrs);
        model.addAttribute("number",addrs.size());
        return "pages/my/address";
    }
}
