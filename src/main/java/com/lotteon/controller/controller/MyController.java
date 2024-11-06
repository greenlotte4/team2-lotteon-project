package com.lotteon.controller.controller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.ArticleDto;
import com.lotteon.dto.responseDto.*;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrdersDto;
import com.lotteon.entity.article.Qna;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.service.article.QnaService;
import com.lotteon.service.member.AddressService;
import com.lotteon.service.member.CustomerService;
import com.lotteon.service.point.CouponService;
import com.lotteon.service.point.CustomerCouponService;
import com.lotteon.service.point.PointService;
import com.lotteon.service.product.OrderService;
import com.lotteon.service.product.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    private final QnaService qnaService;
    private final ReviewService reviewService;


    @ModelAttribute
    public void commonAttributes(Model model) {
        int hasCoupon = customerCouponService.findAllCntByCustomer();
        model.addAttribute("hasCoupon", hasCoupon);
        int hasPoint = customerService.findByCustomer();
        model.addAttribute("hasPoint", hasPoint);
    }

    @GetMapping(value = {"","/","/index"})
    public String index(Model model) {

        Page<ResponseOrdersDto> orders = orderService.selectedMyOrderList(0);


        Page<GetPointsDto> points = pointService.findAllByCustomer(0);
        if(points.isEmpty()){
            model.addAttribute("noPoint",true);
            return "pages/my/index";
        }
        List<GetReviewsDto> reviews = reviewService.findTop3();
        if(reviews!=null){
            model.addAttribute("reviews",reviews);
            model.addAttribute("noReview",false);
        } else {
            model.addAttribute("noReview",true);
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

    // 나의 쇼핑정보 > 나의 설정
    @GetMapping("/info")
    public String info(Model model,Authentication authentication) {
        log.info("컨트롤러 접속 ");
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        Long custId = auth.getUser().getCustomer().getId();
        log.info("커스터머 아이디 "+custId);
        Optional<GetAdminUserDTO> getCust = customerService.myInfo(custId);
        if (getCust.isPresent()) {
            model.addAttribute("cust", getCust.get());
        } else {
            // 예외 처리: 고객 정보가 없는 경우
            log.warn("찾을 수 없는 id : " + custId);
            model.addAttribute("errorMessage",
                    "회원 정보를 찾을 수 없습니다.");
        }
        return "pages/my/info";
    }

    // 나의 쇼핑정보 > 나의 설정 end

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
            orders = orderService.selectedMyOrderList(page);
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

    @GetMapping("/points/use")
    public String usePoint(
            Model model,
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "type",defaultValue = "0") String type,
            @RequestParam(name = "keyword",defaultValue = "0") String keyword
    ) {
        Page<GetPointsDto> points;
        if(!type.equals("0")&&!keyword.equals("0")){
            points = pointService.findAllBySearch2(page,type,keyword);
        } else {
            points = pointService.findAllByCustomer2(page);
        }
        if(points.isEmpty()){
            model.addAttribute("noItem",true);
            return "pages/my/usepoint";
        }
        model.addAttribute("noItem",false);
        model.addAttribute("points", points);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", points.getTotalPages());

        return "pages/my/usepoint";
    }

    @GetMapping("/qnas")
    public String getMyQnas(Model model, Principal principal) {
        MyUserDetails userDetails = (MyUserDetails) ((Authentication) principal).getPrincipal();
        Member memberId = userDetails.getUser(); // MyUserDetails에서 ID를 가져오는 메서드 사용
        List<ArticleDto> qnaList = qnaService.getMyQnas(memberId.getId());
        model.addAttribute("qnaList", qnaList);
        return "pages/my/qna"; // 뷰 파일로 연결
    }

    @GetMapping("/reviews")
    public String review(
            Model model,
            @RequestParam(value = "page",defaultValue = "0") int page
    ) {
        Page<GetReviewsDto> reviews = reviewService.findAll(page);
        if(reviews==null) {
            model.addAttribute("noReview",true);
        } else {
            model.addAttribute("reviews",reviews);
            model.addAttribute("noReview",false);
            model.addAttribute("totalPages", reviews.getTotalPages());
        }
        model.addAttribute("page", page);

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
