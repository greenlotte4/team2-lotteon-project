package com.lotteon.controller.controller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.responseDto.GetCouponDto;
import com.lotteon.dto.responseDto.GetCustomerCouponDto;
import com.lotteon.service.point.CouponService;
import com.lotteon.service.point.CustomerCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/coupon")
@RequiredArgsConstructor
@Log4j2
public class AdminCouponController {

    private final CouponService couponService;
    private final CustomerCouponService customerCouponService;

    private String getSideValue() {
        return "coupon";
    }

    @ModelAttribute
    public void pageIndex(Model model) {
        model.addAttribute("config",getSideValue());
    }

    @GetMapping("/coupons")
    public String coupons(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "searchType", defaultValue = "0") String searchType,
            @RequestParam(name = "keyword", defaultValue = "0") String keyword
    ) {

        model.addAttribute("active","coupons");
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String issuer ;
        if(auth.getUser().getMemRole().equals("admin")) {
            issuer = "관리자";
            model.addAttribute("issuer",issuer);
            model.addAttribute("searchCondition","admin");
        } else if(auth.getUser().getMemRole().equals("seller")) {
            issuer = auth.getUser().getMemUid();
            model.addAttribute("issuer",issuer);
            model.addAttribute("searchCondition","seller");
        }

        if(!searchType.equals("0")&&!keyword.equals("0")) {
            Page<GetCouponDto> coupons = couponService.findAllCouponsBySearch(page,searchType,keyword);
            model.addAttribute("coupons", coupons);
            model.addAttribute("page",page);
            model.addAttribute("totalPages",coupons.getTotalPages());
            model.addAttribute("memId",auth.getUser().getId());
            model.addAttribute("searchType",searchType);
            model.addAttribute("keyword",keyword);

            return "pages/admin/coupon/list";
        }

        Page<GetCouponDto> coupons = couponService.findAllCoupons(page);
          model.addAttribute("coupons", coupons);
        model.addAttribute("page",page);
        model.addAttribute("totalPages",coupons.getTotalPages());
        model.addAttribute("memId",auth.getUser().getId());

        return "pages/admin/coupon/list";
    }

    @GetMapping("/issuearances")
    public String issuances(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "searchType", defaultValue = "0") String searchType,
            @RequestParam(name = "keyword", defaultValue = "0") String keyword
    ) {
        model.addAttribute("active","issuearances");
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Page<GetCustomerCouponDto> custCoupons;
        if(auth.getUser().getMemRole().equals("admin")) {
            custCoupons = customerCouponService.findAll(page,searchType,keyword);
        } else {
            custCoupons = customerCouponService.findAllBySeller(page,searchType,keyword);
        }
        model.addAttribute("custCoupons", custCoupons);
        model.addAttribute("searchCondition",auth.getUser().getMemRole());
        model.addAttribute("page",page);
        model.addAttribute("totalPages",custCoupons.getTotalPages());
        model.addAttribute("memId",auth.getUser().getId());
        model.addAttribute("searchType",searchType);
        model.addAttribute("keyword",keyword);

        return "pages/admin/coupon/issuearance";
    }

}
