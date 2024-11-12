package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.GetDeliveryDto;
import com.lotteon.dto.requestDto.cartOrder.PostCartSaveDto;
import com.lotteon.dto.requestDto.cartOrder.OrderDto;
import com.lotteon.dto.requestDto.cartOrder.PostOrderDeliDto;
import com.lotteon.dto.responseDto.GetDeliInfoDto;
import com.lotteon.dto.responseDto.GetIncomeDto;
import com.lotteon.dto.responseDto.GetPointsDto;
import com.lotteon.dto.responseDto.cartOrder.GetOrderDto;
import com.lotteon.dto.responseDto.cartOrder.*;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.point.CustomerCoupon;
import com.lotteon.entity.point.Point;
import com.lotteon.entity.product.*;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.SellerRepository;
import com.lotteon.repository.point.CustomerCouponRepository;
import com.lotteon.repository.point.PointRepository;
import com.lotteon.repository.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductOptionRepository productOptionRepository;
    private final SellerRepository sellerRepository;
    private final OrderCancleRepository orderCancleRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final CustomerRepository customerRepository;
    private final PointRepository pointRepository;

    public List<GetOrderDto> selectedOrders(List<PostCartSaveDto> selectedProducts) {

        List<GetOrderDto> orderDtos = new ArrayList<>();

        log.info("셀렉티드 프로덕트 보기 "+selectedProducts.toString());
        for(PostCartSaveDto postCartSaveDto : selectedProducts) {

            Long productId = postCartSaveDto.getProductId();
            Optional<Product> product = productRepository.findById(productId);

            ProductDto productDto = ProductDto.builder()
                    .id(product.get().getId())
                    .prodName(product.get().getProdName())
                    .prodDeliver(product.get().getProdDeliver())
                    .prodPrice(product.get().getProdPrice())
                    .prodDiscount(product.get().getProdDiscount())
                    .prodPoint(product.get().getProdPoint())
                    .prodSummary(product.get().getProdSummary())
                    .prodListImg(product.get().getProdListImg())
                    .totalPrice(postCartSaveDto.getTotalPrice())
                    .sellId(product.get().getSeller().getId())
                    .stock(product.get().getProdStock())
                    .build();
            CartItemDto cartItemDto = new CartItemDto();
            if(postCartSaveDto.getCartItemId()!=null) {
                Long cartItemId = postCartSaveDto.getCartItemId();
                Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);
                cartItemDto = CartItemDto.builder()
                        .cartId(cartItem.get().getCart().getId())
                        .id(cartItem.get().getId())
                        .quantity(cartItem.get().getQuantity())
                        .build();

            }else {
                cartItemDto = CartItemDto.builder()
                        .cartId(null)
                        .id(null)
                        .quantity(postCartSaveDto.getQuantity())
                        .build();
            }

            //옵션이 있으면 옵션리스트에 담기
            Long optionId = null;
            List<String> optionValue = new ArrayList<>();
            if(postCartSaveDto.getOptionId()!=null){
                optionId = postCartSaveDto.getOptionId();
                ProductOption option = productOptionRepository.findById(optionId).orElse(null);

                if (option.getOptionValue() != null) {
                    optionValue.add(option.getOptionValue());
                }
                if (option.getOptionValue2() != null) {
                    optionValue.add(option.getOptionValue2());
                }
                if (option.getOptionValue3() != null) {
                    optionValue.add(option.getOptionValue3());
                }
            }
            log.info(" 옵션 밸류 볼래용 "+optionValue.toString());

            GetOrderDto orderDto = GetOrderDto.builder()
                    .products(productDto)
                    .quantity(postCartSaveDto.getQuantity())
                    .optionId(optionId)
                    .optionValue(optionValue)
                    .totalPrice(postCartSaveDto.getTotalPrice())
                    .cartItems(cartItemDto)
                    .build();

            orderDtos.add(orderDto);
        }
        return orderDtos;
    }

    public Order insertOrder(OrderDto orderDto) {

        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails auth =(MyUserDetails) authentication.getPrincipal();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        } else if (authentication.getPrincipal() instanceof MyUserDetails) {
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            // 로그인된 사용자 처리
        }
        String zip = orderDto.getReceiverZip();
        String addr1 = orderDto.getReceiverAddr1();
        String addr2 = orderDto.getReceiverAddr2();

        Order saveorder = Order.builder()
                .customer(auth.getUser().getCustomer())
                .orderPayment(orderDto.getOrderPayment())
                .receiverName(orderDto.getReceiverName())
                .receiverHp(orderDto.getReceiverHp())
                .receiverAddr(zip+"/"+addr1+"/"+addr2)
                .orderReq(orderDto.getOrderReq()==null?null:orderDto.getOrderReq())
                .orderDeli(orderDto.getOrderDeli())
                .orderDiscount(orderDto.getOrderDiscount())
                .orderQuantity(orderDto.getOrderQuantity())
                .orderTotal(orderDto.getOrderTotal())
                .build();

        Order order = orderRepository.save(saveorder);

        if(order==null){
            return null;
        }

        return order;
    }


    public Page<ResponseOrdersDto> selectedMyOrderList(int page) {

        MyUserDetails auth =(MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();

        log.info("오더 서비스 접속");

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));


        Page<Order> orders = orderRepository.findAllByCustomer(customer,pageable);
        log.info("서비스에서 오더스 잘 뽑혔나요?  "+orders.toString());

        // Page<Order> 객체를 map을 이용해 Page<ResponseOrdersDto>로 변환
        Page<ResponseOrdersDto> responseOrdersDtos = orders.map(order -> {
            return ToResponseMyOrderDto(order);
        });

        return responseOrdersDtos;
    }

    public Page<ResponseAdminOrderDto> selectedAdminOrdersBySeller(int page) {

        MyUserDetails auth =(MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller  = auth.getUser().getSeller();


        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        log.info("셀러 "+seller);
        Page<Order> orders = orderRepository.findAllByOrderItems_Seller(seller,pageable);
        Page<ResponseAdminOrderDto> orderDtos = orders.map(order -> {
            return ToResponseAdminOrderDtoBySeller(order, seller);
        });
        log.info("오더 레포지토리 테스트 "+orders.getContent());

        return orderDtos;
    }
    public Page<ResponseAdminOrderDto> findAdminOrdersByKeyword(int page, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> orders;
        if(searchType.equals("custId")){
            orders = orderRepository.findAllByCustomer_Member_MemUidOrderByIdDesc(keyword,pageable);
        } else if (searchType.equals("orderId")){
            orders = orderRepository.findAllByIdOrderByIdDesc(Long.parseLong(keyword),pageable);
        } else {
            orders = orderRepository.findAllByCustomer_CustNameOrderByIdDesc(keyword,pageable);
        }

        Page<ResponseAdminOrderDto> dtos = orders.map(order ->this.ToResponseAdminOrderDtoByAdmin(order));
        return dtos;
    }

    public Page<ResponseAdminOrderDto> findSellerOrdersByKeyword(int page, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC, "id"));

        MyUserDetails auth =(MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller  = auth.getUser().getSeller();

        Page<Order> orders;
        if(searchType.equals("custId")){
            orders = orderRepository.findAllByOrderItems_SellerAndCustomer_Member_MemUidOrderByIdDesc(seller,keyword,pageable);
        } else if (searchType.equals("orderId")){
            orders = orderRepository.findAllByIdAndOrderItems_SellerOrderByIdDesc(Long.parseLong(keyword),seller,pageable);
        } else {
            orders = orderRepository.findAllByOrderItems_SellerAndCustomer_CustNameOrderByIdDesc(seller,keyword,pageable);
        }

        Page<ResponseAdminOrderDto> dtos = orders.map(order ->this.ToResponseAdminOrderDtoBySeller(order,seller));
        return dtos;
    }

    public Page<ResponseAdminOrderDto> selectedAdminOrdersByAdmin(int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<Order> orders = orderRepository.findAll(pageable);
        log.info("파인드올 셀러 아이템  "+orders.getContent());


        Page<ResponseAdminOrderDto> orderDtos = orders.map(order -> {
            return ToResponseAdminOrderDtoByAdmin(order);
        });
        return orderDtos;
    }
    public Page<GetDeliveryDto> findAllBySeller(int page) {
        Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> orders;
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller = auth.getUser().getSeller();
        orders = orderRepository.findAllByOrderItems_SellerAndOrderItems_OrderDeliIdIsNotNullAndOrderItems_OrderDeliCompanyNotNullOrderByIdDesc(seller,pageable);
        Page<GetDeliveryDto> dtos = orders.map(v->v.toGetDeliveryDto());
        return dtos;
    }

    public Page<GetDeliveryDto> findAllBySellerAndSearchType(int page, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> orders;
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller = auth.getUser().getSeller();
        if(searchType.equals("deliId")){
            orders = orderRepository.findAllByOrderItems_SellerAndOrderItems_OrderDeliIdAndOrderItems_OrderDeliCompanyNotNullOrderByIdDesc(seller,keyword,pageable);
        } else if (searchType.equals("orderId")){
            orders = orderRepository.findAllByOrderItems_SellerAndIdAndOrderItems_OrderDeliIdIsNotNullAndOrderItems_OrderDeliCompanyNotNullOrderByIdDesc(seller,Long.parseLong(keyword),pageable);
        } else {
            orders = orderRepository.findAllByOrderItems_SellerAndReceiverNameAndOrderItems_OrderDeliIdIsNotNullAndOrderItems_OrderDeliCompanyNotNullOrderByIdDesc(seller,keyword,pageable);
        }
        Page<GetDeliveryDto> dtos = orders.map(v->v.toGetDeliveryDto());
        return dtos;
    }

    public Boolean updateOrderDeli(PostOrderDeliDto postOrderDeliDto) {
        log.info("서비스 배송정보 업데이트 "+postOrderDeliDto.toString());

        Optional<Order> optOrder= orderRepository.findById(postOrderDeliDto.getOrderId());
        if(optOrder.isEmpty()){
            return false;
        }
        Optional<OrderItem> orderItem = orderItemRepository.findById(postOrderDeliDto.getOrderItemId());
        if(orderItem.isEmpty()){
            return false;
        }
        LocalDate today = LocalDate.now();
        orderItem.get().setState2(1);
        orderItem.get().setOrderDeliId(postOrderDeliDto.getOrderDeliId());
        orderItem.get().setOrderDeliCompany(postOrderDeliDto.getOrderDeli());
        orderItem.get().setOrderDeliSdate(today);
        if(optOrder.get().getOrderState()==0){
            optOrder.get().setOrderState(1);
        }
        return true;
    }

    public GetDeliInfoDto findByDeliveryId(String deliveryId) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller = auth.getUser().getSeller();
        Optional<Order> order = orderRepository.findByOrderItems_SellerAndOrderItems_OrderDeliIdAndOrderItems_OrderDeliCompanyNotNull(seller,deliveryId);
        System.out.println(order.get().toGetDeliInfoDto());
        return order.get().toGetDeliInfoDto();
    }

    public Page<ResponseOrdersDto> findAllBySearch(int page, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, 10);
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();

        Page<Order> orders;
        if(type.equals("date")){
            orders = this.findAllByDate(pageable,customer,keyword);
        } else if(type.equals("custom")){
            orders = this.findAllByCustom(pageable,customer,keyword);
        }else {
            orders = this.findAllByMonth(pageable,customer,keyword);
        }
        Page<ResponseOrdersDto> dtos = orders.map(order->

                ResponseOrdersDto.builder()
                        .orderId(order.getId())
                        .orderQuantity(order.getOrderQuantity())
                        .orderState(order.getOrderState())
                        .orderTotal(order.getOrderTotal())
                        .orderRdate( new SimpleDateFormat("yyyy-MM-dd").format(order.getOrderRdate()))
                        .prodId(order.getOrderItems().get(0).getProduct().getId())
                        .prodListImg(order.getOrderItems().get(0).getProduct().getProdListImg())
                        .prodName(order.getOrderItems().get(0).getProduct().getProdName())
                        .seller(order.getOrderItems().get(0).getSeller().getSellCompany())
                        .orderItemCount(order.getOrderItems().size())
                        .build()
        );
        return dtos;
    }

    private Page<Order> findAllByCustom(Pageable pageable, Customer customer, String keyword) {

        String sDate = keyword.substring(0,keyword.indexOf("~"));
        String eDate = keyword.substring(keyword.indexOf("~")+1);
        Timestamp startDate = Timestamp.valueOf(LocalDate.parse(sDate).atStartOfDay());
        Timestamp endDate = Timestamp.valueOf(LocalDate.parse(eDate).atStartOfDay().plusDays(1).minusNanos(1)); // 하루의 끝까지 포함하도록 설정
        Page<Order> orders = orderRepository.findAllByCustomerAndOrderRdateBetweenOrderByIdDesc(customer,startDate,endDate,pageable);
        return orders;
    }

    private Page<Order> findAllByMonth(Pageable pageable, Customer customer, String keyword) {
        Timestamp today = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusDays(1).minusNanos(1));
        Timestamp varDay = Timestamp.valueOf(LocalDate.now().minusMonths(Integer.parseInt(keyword)).atStartOfDay());
        Page<Order> orders = orderRepository.findAllByCustomerAndOrderRdateBetweenOrderByIdDesc(customer,varDay,today,pageable);
        return orders;
    }

    private Page<Order> findAllByDate(Pageable pageable, Customer customer, String keyword) {
        Timestamp today = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusDays(1).minusNanos(1));
        Timestamp varDay = Timestamp.valueOf(LocalDate.now().minusDays(Integer.parseInt(keyword)).atStartOfDay());
        Page<Order> orders = orderRepository.findAllByCustomerAndOrderRdateBetweenOrderByIdDesc(customer,varDay,today,pageable);
        return orders;
    }

    public Page<GetIncomeDto> findIncome(int page) {
        Pageable pageable = PageRequest.of(page, 6);
        Page<Seller> sellers;
        sellers = sellerRepository.findAll(pageable);
        Page<GetIncomeDto> dtos = sellers.map(Seller::toGetIncomeDto);
        return dtos;
    }

    public Page<GetIncomeDto> findIncomeSearchType(int page, String searchType) {
        Pageable pageable = PageRequest.of(page, 6);

        Page<GetIncomeDto> dtos;
        Page<Seller> sellers = sellerRepository.findAll(pageable);
        if(searchType.equals("day")){
            dtos = sellers.map(Seller::toGetIncomeDto);
        } else if(searchType.equals("week")){
            dtos = sellers.map(Seller::toGetIncomeDto2);
        } else {
            dtos = sellers.map(Seller::toGetIncomeDto3);
        }
        return dtos;
    }
    //ToDto 변환 메서드
    public ResponseOrdersDto ToResponseMyOrderDto(Order order){
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(order.getOrderRdate());
        return ResponseOrdersDto.builder()
                .orderId(order.getId())
                .orderQuantity(order.getOrderQuantity())
                .orderState(order.getOrderState())
                .orderTotal(order.getOrderTotal())
                .orderRdate(formattedDate)
                .prodId(order.getOrderItems().get(0).getProduct().getId())
                .prodListImg(order.getOrderItems().get(0).getProduct().getProdListImg())
                .prodName(order.getOrderItems().get(0).getProduct().getProdName())
                .seller(order.getOrderItems().get(0).getSeller().getSellCompany())
                .orderItemCount(order.getOrderItems().size())
                .build();
    }
    public ResponseAdminOrderDto ToResponseAdminOrderDtoBySeller(Order order, Seller seller){
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(order.getOrderRdate());
        // OrderItems에서 seller ID로 필터링
        List<OrderItem> sellerOrderItems = order.getOrderItems().stream()
                .filter(item -> item.getSeller().getId().equals(seller.getId()))
                .collect(Collectors.toList());
        // 필터링된 아이템의 개수
        int sellerOrderItemCount = sellerOrderItems.size();
        // 필터링된 아이템의 총 가격 합산
        int sellerOrderTotal = sellerOrderItems.stream()
                .mapToInt(OrderItem::getTotal) // OrderItem의 total 필드 사용
                .sum();
        return ResponseAdminOrderDto.builder()
                .orderId(order.getId())
                .OrderRdate(formattedDate)
                .OrderState(order.getOrderItems().get(0).getState2())
                .OrderItemCount(sellerOrderItemCount)
                .OrderItemTotal(sellerOrderTotal)
                .memUid(order.getCustomer().getMember().getMemUid())
                .custName(order.getCustomer().getCustName())
                .ProdName(order.getOrderItems().get(0).getProduct().getProdName())
                .build();
    }
    public ResponseAdminOrderDto ToResponseAdminOrderDtoByAdmin(Order order){

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(order.getOrderRdate());

        // 필터링된 아이템의 개수
        int sellerOrderItemCount = order.getOrderItems().size();

        // 필터링된 아이템의 총 가격 합산
        int sellerOrderTotal = order.getOrderItems().stream()
                .mapToInt(OrderItem::getTotal) // OrderItem의 total 필드 사용
                .sum();

        return ResponseAdminOrderDto.builder()
                .orderId(order.getId())
                .OrderRdate(formattedDate)
                .OrderState(order.getOrderItems().get(0).getState2())
                .OrderItemCount(sellerOrderItemCount)
                .OrderItemTotal(sellerOrderTotal)
                .memUid(order.getCustomer().getMember().getMemUid())
                .custName(order.getCustomer().getCustName())
                .ProdName(order.getOrderItems().get(0).getProduct().getProdName())
                .build();
    }

    @Scheduled(cron = "0 13 20 * * *")
    public void updateOrderItemState(){
        LocalDate threeDaysAgo = LocalDate.now().minusDays(2);

        List<OrderItem> orderItems = orderItemRepository.findAllByDeliSdateBefore(threeDaysAgo);
        for(OrderItem orderItem : orderItems){
            if(orderItem.getState2()==1){
                orderItem.setState2(4);
                if(orderItem.getOrder().getOrderState()==1){
                    orderItem.getOrder().setOrderState(2);
                }
            }
        }
    }

    public void cancleOrder(Long id) {

        Optional<OrderCancleDocument> orderCancleDocument = orderCancleRepository.findByOrderId(id);
        Long couponId = orderCancleDocument.get().getCouponId();
        int points = orderCancleDocument.get().getPoints();
        Long custId = orderCancleDocument.get().getCustId();
        Long orderId = orderCancleDocument.get().getOrderId();
        LocalDateTime time = orderCancleDocument.get().getPointUdate();
        LocalDateTime before = time.minusMinutes(2);
        LocalDateTime after = time.plusMinutes(2);
        if(couponId!=0){
            Optional<CustomerCoupon> coupon = customerCouponRepository.findById(couponId);
            coupon.get().updateCustCouponState(1);
        }

        if(points!=0){
            List<Point> pointLists = pointRepository.findAllByCustomer_IdAndPointTypeAndPointUdateBetween(custId,2,before,after);
            pointLists.forEach(v->{
                v.updateReUsePoint();
            });
        }
        Point point = pointRepository.findById(orderCancleDocument.get().getPointId()).get();
        pointRepository.delete(point);

        Optional<Point> point2 = pointRepository.findFirstByOrderIdAndPointEtc(orderId,"사용후남은포인트");
        if(point2.isPresent()){
            pointRepository.delete(point2.get());
        }

        Optional<Point> point3 = pointRepository.findFirstByOrderIdAndPointType(orderId,3);
        pointRepository.delete(point3.get());
    }

    public Long findByCustomer() {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return orderRepository.countByCustomer(auth.getUser().getCustomer());
    }
}
