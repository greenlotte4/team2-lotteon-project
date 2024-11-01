package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.GetDeliveryDto;
import com.lotteon.dto.requestDto.cartOrder.PostCartSaveDto;
import com.lotteon.dto.requestDto.cartOrder.OrderDto;
import com.lotteon.dto.requestDto.cartOrder.PostOrderDeliDto;
import com.lotteon.dto.responseDto.GetDeliInfoDto;
import com.lotteon.dto.responseDto.GetPointsDto;
import com.lotteon.dto.responseDto.cartOrder.GetOrderDto;
import com.lotteon.dto.responseDto.cartOrder.*;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.point.Point;
import com.lotteon.entity.product.*;
import com.lotteon.repository.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    /*
        날짜:
        이름: 박연화

     */

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CartItemOptionRepository cartItemOptionRepository;
    private final ModelMapper modelMapper;

    public List<GetOrderDto> selectedOrders(List<PostCartSaveDto> selectedProducts) {

        List<GetOrderDto> orderDtos = new ArrayList<>();

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
                            .build();

        Order order = orderRepository.save(saveorder);

        if(order==null){
            return null;
        }

        return order;
    }


    public Page<ResponseOrdersDto> selectedOrderList(int page) {

        MyUserDetails auth =(MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();

        log.info("오더 서비스 접속");

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));


        Page<Order> orders = orderRepository.findAllByCustomer(customer,pageable);
        log.info("서비스에서 오더스 잘 뽑혔나요?  "+orders.toString());

        // Page<Order> 객체를 map을 이용해 Page<ResponseOrdersDto>로 변환
        Page<ResponseOrdersDto> responseOrdersDtos = orders.map(order -> {
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

            String formattedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(order.getOrderRdate());

            // OrderItems에서 seller ID로 필터링
            List<OrderItem> sellerOrderItems = order.getOrderItems().stream()
                    .filter(item -> item.getSeller().getId().equals(seller.getId()))
                    .collect(Collectors.toList());

            log.info("셀러 아이템 필터링한 거 "+sellerOrderItems.toString());

            // 필터링된 아이템의 개수
            int sellerOrderItemCount = sellerOrderItems.size();

            log.info("셀러 아이템 개수 "+sellerOrderItemCount);


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

        });
        log.info("오더 레포지토리 테스트 "+orders.getContent());

        return orderDtos;
    }

    public Page<ResponseAdminOrderDto> selectedAdminOrdersByAdmin(int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<Order> orders = orderRepository.findAll(pageable);
        log.info("파인드올 셀러 아이템  "+orders.getContent());


        Page<ResponseAdminOrderDto> orderDtos = orders.map(order -> {

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

        });
        return orderDtos;
    }

    public Boolean updateOrderDeli(PostOrderDeliDto postOrderDeliDto) {
        log.info("서비스 배송정보 업데이트 "+postOrderDeliDto.toString());

        Optional<Order> optOrder= orderRepository.findById(postOrderDeliDto.getOrderId());

        log.info("서비스 오더아이디로 오더 찾기 "+optOrder.toString());
        if(optOrder.isPresent()) {
            List<OrderItem> orderItems = optOrder.get().getOrderItems();
            for(OrderItem orderItem : orderItems) {
                orderItem.setState2(postOrderDeliDto.getOrderState());
                orderItem.setOrderDeliId(postOrderDeliDto.getOrderDeliId());
                orderItem.setOrderDeliCompany(postOrderDeliDto.getOrderDeli());
            }
            return true;
        }else{
            return false;
        }
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

    public GetDeliInfoDto findByDeliveryId(String deliveryId) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller = auth.getUser().getSeller();
        Optional<Order> order = orderRepository.findByOrderItems_SellerAndOrderItems_OrderDeliIdAndOrderItems_OrderDeliCompanyNotNull(seller,deliveryId);
        System.out.println("===================");
        System.out.println(order.get().toGetDeliInfoDto());
        return order.get().toGetDeliInfoDto();
    }

    public Page<ResponseOrdersDto> findAllBySearch(int page, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, 5);
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
        Page<Order> orders = orderRepository.findAllByCustomerAndOrderRdateBetweenOrderByIdAsc(customer,startDate,endDate,pageable);
        return orders;
    }

    private Page<Order> findAllByMonth(Pageable pageable, Customer customer, String keyword) {
        Timestamp today = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusDays(1).minusNanos(1));
        Timestamp varDay = Timestamp.valueOf(LocalDate.now().minusMonths(Integer.parseInt(keyword)).atStartOfDay());
        Page<Order> orders = orderRepository.findAllByCustomerAndOrderRdateBetweenOrderByIdAsc(customer,varDay,today,pageable);
        return orders;
    }

    private Page<Order> findAllByDate(Pageable pageable, Customer customer, String keyword) {
        Timestamp today = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusDays(1).minusNanos(1));
        Timestamp varDay = Timestamp.valueOf(LocalDate.now().minusDays(Integer.parseInt(keyword)).atStartOfDay());
        Page<Order> orders = orderRepository.findAllByCustomerAndOrderRdateBetweenOrderByIdAsc(customer,varDay,today,pageable);
        return orders;
    }
}
