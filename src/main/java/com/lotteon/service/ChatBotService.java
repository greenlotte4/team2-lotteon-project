package com.lotteon.service;


import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.responseDto.GetDeliveryDateDto;
import com.lotteon.entity.category.CategoryProduct;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.point.CustomerCoupon;
import com.lotteon.entity.product.Order;
import com.lotteon.entity.product.OrderItem;
import com.lotteon.entity.product.Product;
import com.lotteon.repository.category.CategoryProductRepository;
import com.lotteon.repository.point.CustomerCouponRepository;
import com.lotteon.repository.product.OrderItemRepository;
import com.lotteon.repository.product.OrderRepository;
import com.lotteon.service.product.OrderItemService;
import com.lotteon.service.product.OrderService;
import com.lotteon.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatBotService {

    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryProductRepository categoryProductRepository;
    private final OrderItemService orderItemService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerCouponRepository customerCouponRepository;


    public String findBestProduct(String prompt) {
        Pattern pattern = Pattern.compile("(\\d+)개만 추천");
        Matcher matcher = pattern.matcher(prompt);
        int recommendedCount = 5; // 기본값 5로 설정

        if (matcher.find()) {
            recommendedCount = Integer.parseInt(matcher.group(1));
        }

        List<Product> products = productService.getRecommendedProducts(recommendedCount);

        String productList = products.stream()
                .map(product -> product.getProdName() + " - " + product.getProdPrice() + "원")
                .collect(Collectors.joining(", "));

        // OpenAI에 전달할 새로운 프롬프트 생성 (상품 추천 결과를 포함)
        return "추천 상품: " + productList;
    }

    public String findBestCategory(String prompt) {
        Pattern pattern = Pattern.compile("(\\d+)개만 추천");
        Matcher matcher = pattern.matcher(prompt);
        int recommendedCount = 5; // 기본값 5로 설정

        if (matcher.find()) {
            recommendedCount = Integer.parseInt(matcher.group(1));
        }
        String regex = "화장품|가전|의류|식품|도서|여성의류|남성의류|브랜드의류|악기|취미용품|스포츠브랜드|로션|스마트폰|상의|캐쥬얼브랜드|유아의류|선크림|라면|자켓|코트|스킨|원피스|삼성|아이폰";

        // 카테고리가 있는지 체크
        Pattern categoryPattern = Pattern.compile(regex);
        Matcher categoryMatcher = categoryPattern.matcher(prompt);

        if (!categoryMatcher.find()) {
            return "입력한 카테고리와 유사한 카테고리가 없습니다.";
        }
        String categoryName = categoryMatcher.group();

        Optional<CategoryProduct> category = categoryProductRepository.findByCategoryNameContaining(categoryName);
        Long categoryId = category.get().getCategoryId();

        List<Product> products = productService.getRecommendedProductsAndCate(recommendedCount,categoryId);
        if(products.isEmpty()){
            return "해당 카테고리에 추천할만한 상품이 없습니다. 원인 : 카테고리내 상품 없음";
        }
        String productList = products.stream()
                .map(product -> product.getProdName() + " - " + product.getProdPrice() + "원" + " 할인율이 무려" + product.getProdDiscount() + "%!!!")
                .collect(Collectors.joining(", "));

        return "추천 상품: " + productList;
    }

    public String cancleOrder(String prompt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        Long orderId = Long.parseLong(prompt.substring(prompt.indexOf(":")+1).trim());
        orderItemService.patchCancelState(orderId,6,0,5);
        orderService.cancleOrder(orderId);
        return "주문번호 : "+orderId +"를 주문 취소하였습니다.";
    }

    public String possibleOrder(String prompt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        List<Order> orders = orderRepository.findAllByOrderStateAndCustomer(0,customer);
        if(orders.isEmpty()){
            return "최소가능한 주문이 없습니다. 배송이나 교환 서비스를 이용해주세요.";
        }
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        String orderIdsString = orderIds.stream()
                .map(String::valueOf) // Long 값을 String으로 변환
                .collect(Collectors.joining(","));
        return "취소가능한 주문번호 : "+orderIdsString;
    }

    public String possibleDeli(String prompt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        List<Order> orders = orderRepository.findAllByOrderStateAndCustomer(1,customer);
        if(orders.isEmpty()){
            return "배송조회 가능한 주문이 없습니다. 판매자에게 발송을 문의해주세요.";
        }
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        String orderIdsString = orderIds.stream()
                .map(String::valueOf) // Long 값을 String으로 변환
                .collect(Collectors.joining(","));
        return "배송조회가능한 주문번호 : "+orderIdsString;
    }

    public String selectDeliDate(String prompt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        Long orderId = Long.parseLong(prompt.substring(prompt.indexOf(":")+1).trim());
        List<GetDeliveryDateDto> deliveryDate = orderItemService.findDeliveryDateAllByOrderId(orderId);
        if(deliveryDate.isEmpty()){
            return "배송조회 가능한 주문이 없습니다. 판매자에게 발송을 문의해주세요.";
        }
        String result = "";
        for(GetDeliveryDateDto dto : deliveryDate){
            result += dto.getProdName() + "상품의 배송예정일 :" + dto.getDate();
        }
        return result;
    }

    public String possibleReturn(String prompt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        List<Order> orders = orderRepository.findAllByOrderStateAndCustomer(2,customer);
        if(orders.isEmpty()){
            return "반품가능한 주문이 없습니다. \n 자세한 내역은 마이페이지에서 확인해주세요.";
        }
        List<Long> orderIds = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .filter(orderItem -> orderItem.getState2() == 4)
                .map(OrderItem::getId)
                .toList();

        String orderIdsString = orderIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return "반품가능한 주문번호 : " + orderIdsString;
    }

    public String selectReturn(String prompt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        Long orderId = Long.parseLong(prompt.substring(prompt.indexOf(":")+1).trim());
        orderItemService.patchItemState(orderId,2,1,3);

        return "주문번호 : " + orderId + "번의 상품이 반품되었습니다.";
    }

    public String possibleChange(String prompt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        List<Order> orders = orderRepository.findAllByOrderStateAndCustomer(2,customer);
        if(orders.isEmpty()){
            return "교환가능한 주문이 없습니다. \n 자세한 내역은 마이페이지에서 확인해주세요.";
        }
        List<Long> orderIds = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .filter(orderItem -> orderItem.getState2() == 4)
                .map(OrderItem::getId)
                .toList();

        String orderIdsString = orderIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return "교환가능한 주문번호 : " + orderIdsString;
    }

    public String selectChange(String prompt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        Long orderId = Long.parseLong(prompt.substring(prompt.indexOf(":")+1).trim());
        orderItemService.patchItemState(orderId,3,1,3);

        return "주문번호 : " + orderId + "번의 상품이 교환신청 되었습니다. 자세한 사항은 판매자에게 문의하세요.";
    }

    public String possibleCustomerCoupon() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        List<CustomerCoupon> coupons = customerCouponRepository.findAllByCustomerAndCouponState(customer,1);
        if(coupons.isEmpty()){
            return "사용가능한 쿠폰이 없습니다.";
        }
        List<String> couponNames = coupons.stream().map(v->v.getCoupon().getCouponName()).collect(Collectors.toList());
        String couponNameStr = couponNames.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return "사용가능한 쿠폰은 : " + couponNameStr + "입니다.";
    }

    public String findAllCustomerPoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "로그인 후 이용가능한 서비스입니다.";
        }
        MyUserDetails auth = (MyUserDetails) authentication.getPrincipal();
        Customer customer = auth.getUser().getCustomer();

        return "사용가능한 포인트 : "+customer.getCustPoint();
    }
}