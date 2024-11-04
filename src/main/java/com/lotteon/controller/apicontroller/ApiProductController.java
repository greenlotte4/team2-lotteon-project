package com.lotteon.controller.apicontroller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.GetProductNamesDto;
import com.lotteon.dto.requestDto.PostReviewDto;
import com.lotteon.dto.requestDto.cartOrder.*;
import com.lotteon.dto.requestDto.PostCouponDto;
import com.lotteon.dto.requestDto.cartOrder.OrderDto;
import com.lotteon.dto.requestDto.cartOrder.OrderItemDto;
import com.lotteon.dto.requestDto.cartOrder.PostOrderDto;
import com.lotteon.dto.responseDto.GetMainProductDto;
import com.lotteon.dto.responseDto.GetOption1Dto;
import com.lotteon.entity.product.Order;
import com.lotteon.repository.member.UserLogRepository;
import com.lotteon.service.member.UserLogService;
import com.lotteon.service.point.CouponService;
import com.lotteon.entity.product.Cart;
import com.lotteon.service.point.CustomerCouponService;
import com.lotteon.service.point.PointService;
import com.lotteon.service.product.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/prod")
@RequiredArgsConstructor
public class ApiProductController {
    private final CustomerCouponService customerCouponService;
    private final CartService cartService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final PointService pointService;
    private final CouponService couponService;
    private final UserLogRepository userLogRepository;
    private final UserLogService userLogService;
    private final ProductOptionService productOptionService;
    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping("/test/coupon")
    public void toTestCouponIssue(){

        customerCouponService.useCustCoupon();
    }

    @GetMapping("/option2")
    public ResponseEntity<?> getOption2(
            @RequestParam String optionValue,
            @RequestParam Long prodId
    ){
        List<GetOption1Dto> options = productOptionService.findByOptionValue(optionValue,prodId);
        Map<String,Object> map = new HashMap<>();
        map.put("option2s",options);
        return ResponseEntity.ok().body(map);
    }

    @PostMapping("/customer/coupon")
    public ResponseEntity<?> getCustCoupon(@RequestBody PostCouponDto dto){
        String result;

        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        String cacheKey = "dailyCoupon::daily_" + dto.getId() + "_" + auth.getUser().getId() +"_" + LocalDate.now();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
            return ResponseEntity.ok("이미 수령하였습니다.");
        }

        String dailyCoupon = customerCouponService.postDailyCoupon(dto.getId(),auth);

        return ResponseEntity.ok(dailyCoupon);
    }

    @PostMapping("/cart")
    public ResponseEntity<?> cartInsert(@RequestBody PostCartDto postCartDto, Authentication authentication, HttpSession session) {
        log.info("카트 컨트롤러 접속 "+postCartDto.toString());
        Map<String, String> response = new HashMap<>();

        if(authentication==null){
            log.info("비회원임!!!");
            response.put("status", "noAuth");
            return ResponseEntity.ok().body(response);
        }
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        String role = auth.getUser().getMemRole();

        if(role.equals("admin") || role.equals("seller")){
            response.put("status", "seller");
        }else{
            ResponseEntity result = cartService.insertCart(postCartDto, session);
            Cart cart = (Cart) result.getBody();
            ResponseEntity result2 = cartService.insertCartItem(postCartDto,cart);
            response.put("status", "customer"); // Add a default response
        }
        if(auth.getUser() != null) {
            userLogService.saveUserLog(auth.getUser().getCustomer().getId(),postCartDto.getProdId(),"cart");
        }
        return ResponseEntity.ok(response);
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

    @PostMapping("/order/direct")
    public ResponseEntity<?> viewToOrder(@RequestBody PostCartSaveDto selectedProduct,
                                               HttpSession session,
                                               Authentication authentication){
        log.info("선택한 카트 정보 "+selectedProduct.toString());
        Map<String, String> response = new HashMap<>();
        if(authentication==null){
            log.info("비회원임!!!");
            response.put("status", "noAuth");
            return ResponseEntity.ok().body(response);
        }
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        String role = auth.getUser().getMemRole();

        List<PostCartSaveDto> selectedProducts = new ArrayList<>();
        selectedProducts.add(selectedProduct);

        if(role.equals("admin") || role.equals("seller")){
            response.put("status", "seller");
        }else{
            session.setAttribute("selectedProducts", selectedProducts);
            response.put("status", "customer");
        }
        if(auth.getUser() != null) {
            selectedProducts.forEach(v->{
                userLogService.saveUserLog(auth.getUser().getCustomer().getId(),v.getProductId(),"order");
            });
        }
        return ResponseEntity.ok(response);

    }

    @PostMapping("/order")
    public ResponseEntity order(@RequestBody PostOrderDto postOrderDto, HttpSession session){
        log.info("컨트롤러에 들어왔나요?"+postOrderDto.toString());
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //카트 세션 꺼내서 null처리하고 삭제
        List<PostCartSaveDto> selectedProducts = (List<PostCartSaveDto>) session.getAttribute("selectedProducts");

        List<Long> cartItemIds = new ArrayList<>();
        for(PostCartSaveDto postCartSaveDto : selectedProducts){
            if(postCartSaveDto.getCartItemId()!=null){
                Long cartItemId = postCartSaveDto.getCartItemId();
                cartItemIds.add(cartItemId);
            }
        }
        if(!cartItemIds.isEmpty()){
            cartService.deleteCartItem(cartItemIds);
        }
        log.info("카트 아이템 아이디 세션에 저장된 거 "+cartItemIds.toString());
        session.removeAttribute("selectedProducts");


        //postOrderDto 꺼내기
        OrderDto orderDto = postOrderDto.getOrderDto();
        List<OrderItemDto> orderItemDto = postOrderDto.getOrderItemDto();

        ResponseEntity orderItemResult = orderItemService.insertOrderItem(orderItemDto,orderDto,session);
        selectedProducts.forEach(v->{
            userLogService.saveUserLog(auth.getUser().getCustomer().getId(),v.getProductId(),"order");
        });
        if(postOrderDto.getOrderPointAndCouponDto().getPoints()!=0){
            pointService.usePoint(postOrderDto.getOrderPointAndCouponDto().getPoints());
        }
        if(postOrderDto.getOrderPointAndCouponDto().getCouponId()!=0){
            customerCouponService.useCoupon(postOrderDto.getOrderPointAndCouponDto().getCouponId());
        }


        productService.top3UpdateBoolean();

        return orderItemResult;
    }

    @GetMapping("/main")
    public ResponseEntity<?> getMainPage(
            @RequestParam String type
    ){
        List<GetMainProductDto> products;
        Map<String,Object> map = new HashMap<>();
        if(type.equals("bestRank")){
            products = productService.findBestItem();
        } else if(type.equals("hit")){
            products = productService.findHitItem();
        } else if(type.equals("recent")){
            products = productService.findRecentItem();
        } else if(type.equals("recommend")) {
            products = productService.findRecommendItem();
        } else if(type.equals("discount")){
            products = productService.findDiscountItem();
        } else {
            products = productService.findSavePointItem();
        }
        map.put("products",products);

        return ResponseEntity.ok(map);
    }

    @PostMapping("/review")
    public ResponseEntity<?> insertReview(
            @RequestBody PostReviewDto review
    ){
        String result = reviewService.addReview(review);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/review/names")
    public ResponseEntity<?> findReviewNames(
            @RequestParam Long orderId
    ){
        List<GetProductNamesDto> names = productService.findReviewNames(orderId);
        Map<String,Object> map = new HashMap<>();
        map.put("names",names);
        return ResponseEntity.ok(map);
    }

}
