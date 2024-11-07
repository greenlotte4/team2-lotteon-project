package com.lotteon.controller.controller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.ArticleDto;
import com.lotteon.dto.responseDto.*;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrdersDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
@Log4j2
public class MyController {

    private final OrderService orderService;
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

    @GetMapping(value = {"", "/", "/index"})
    public String index(Model model, @AuthenticationPrincipal MyUserDetails myUserDetails) {

        Page<ResponseOrdersDto> orders = orderService.selectedMyOrderList(0);


        Page<GetPointsDto> points = pointService.findAllByCustomer(0);
        if (points.isEmpty()) {
            model.addAttribute("noPoint", true);
            return "pages/my/index";
        }
        List<GetReviewsDto> reviews = reviewService.findTop3();
        if (reviews != null) {
            model.addAttribute("reviews", reviews);
            model.addAttribute("noReview", false);
        } else {
            model.addAttribute("noReview", true);
        }

        // 최신 QnA 5개 불러오기
        // QnaService에서 Qna를 ArticleDto로 변환하여 가져오도록 수정
        List<ArticleDto> qnaList = qnaService.getTop5Qnas().stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
        model.addAttribute("qnaList", qnaList);

        // 로그인이 됐을 경우
        if (myUserDetails != null) {
            // customer 가져옴
            Customer customer = myUserDetails.getUser().getCustomer();
            // customer email 출력
            System.out.println("customer.getCustEmail() = " + customer.getCustEmail());
            // model에 email 추가
            model.addAttribute("email", customer.getCustEmail());
            // model에 memId 추가
            model.addAttribute("memId", myUserDetails.getUser().getId());
        }


        model.addAttribute("orders", orders);
        model.addAttribute("points", points);
        model.addAttribute("noPoint",false);


        List<GetAddressDto> addrs = addressService.findAllByCustomer();
        GetMyInfoDTO getCust = customerService.myInfo();
        model.addAttribute("addrs",addrs);
        model.addAttribute("cust",getCust);

        return "pages/my/index";
    }

    @PostMapping(value = {"", "/", "/index"})
    public String indexPost(ArticleDto articleDto) {
        qnaService.insertQnaToSeller(articleDto);
        return "redirect:/my/index";
    }

    @GetMapping("/coupons")
    public String coupon(
            Model model,
            @RequestParam(name = "page",defaultValue = "0") int page
    ) {
        Page<GetMyCouponDto> coupons = customerCouponService.findAllByCustomer(page);
        if(coupons==null){
            model.addAttribute("noItem","true");
            return "pages/my/coupon";
        }
        model.addAttribute("noItem","false");
        model.addAttribute("coupons", coupons);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", coupons.getTotalPages());

        return "pages/my/coupon";
    }

    // 나의 쇼핑정보 > 나의 설정
    @GetMapping("/info")
<<<<<<< Updated upstream
    public String info(Model model) {
        log.info("컨트롤러 접속 111");

        GetMyInfoDTO getCust = customerService.myInfo();

        model.addAttribute("cust",getCust);

=======
    public String info(Model model,Authentication authentication) {
        log.info("컨트롤러 접속 ");

        GetMyInfoDTO getCust = customerService.myInfo();
        if(getCust!=null){
            model.addAttribute("cust",getCust);
        } else {
            return "/";
        }
>>>>>>> Stashed changes
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
        if(points==null){
            model.addAttribute("noItem","true");
            return "pages/my/point";
        }
        model.addAttribute("noItem","false");
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
        if(points==null){
            model.addAttribute("noItem","true");
            return "pages/my/usepoint";
        }
        model.addAttribute("noItem","false");
        model.addAttribute("points", points);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", points.getTotalPages());

        return "pages/my/usepoint";
    }

    /* @GetMapping("/qnas")
    public String getMyQnas(Model model, Principal principal) {
        MyUserDetails userDetails = (MyUserDetails) ((Authentication) principal).getPrincipal();
        Member memberId = userDetails.getUser(); // MyUserDetails에서 ID를 가져오는 메서드 사용
        List<ArticleDto> qnaList = qnaService.getMyQnas(memberId.getId());
        model.addAttribute("qnaList", qnaList);
        return "pages/my/qna"; // 뷰 파일로 연결
    }*/

    @GetMapping("/qnas")
    public String getMyQnas(Model model, Principal principal,
                            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        // 현재 로그인된 사용자 정보를 가져옴
        MyUserDetails userDetails = (MyUserDetails) ((Authentication) principal).getPrincipal();
        Member member = userDetails.getUser();

        // 1. 로그인한 사용자의 페이징된 QnA 목록을 가져옴
        Page<ArticleDto> qnasPage = qnaService.getMyQnas(member.getId(), pageable);

        // 2. 현재 페이지의 QnA 목록 추출
        List<ArticleDto> qnaList = qnasPage.getContent();

        // 3. 전체 QnA 글 개수 가져오기
        long totalQnaCount = qnasPage.getTotalElements();

        // 4. 모델에 페이징 정보와 데이터 추가
        model.addAttribute("qnasPage", qnasPage);       // 전체 페이지 정보 추가
        model.addAttribute("qnaList", qnaList);         // 현재 페이지 QnA 목록 추가
        model.addAttribute("totalQnaCount", totalQnaCount); // 전체 QnA 글 개수 추가

        // 5. 마이페이지 QnA 목록 페이지 반환
        return "pages/my/qna"; // 마이페이지 QnA 목록 뷰 파일
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
