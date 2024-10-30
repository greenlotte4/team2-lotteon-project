package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.cartOrder.OrderDto;
import com.lotteon.dto.requestDto.cartOrder.OrderItemDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderItemDto;
import com.lotteon.dto.responseDto.cartOrder.UserOrderDto;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.product.Order;
import com.lotteon.entity.product.OrderItem;
import com.lotteon.entity.product.Product;
import com.lotteon.repository.impl.OrderItemRepositoryImpl;
import com.lotteon.repository.member.SellerRepository;
import com.lotteon.repository.product.OrderItemRepository;
import com.lotteon.repository.product.OrderRepository;
import com.lotteon.repository.product.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final OrderItemRepositoryImpl orderItemRepositoryImpl;


    public ResponseEntity insertOrderItem(List<OrderItemDto> orderItemDto, OrderDto orderDto, HttpSession session) {
        log.info("오더아이템 서비스 들어옴 ");

        Order order = orderService.insertOrder(orderDto);
        log.info("오더 저장 성공 : "+order);

        if(order==null){
            ResponseEntity.badRequest().body("로그인 해야 함");
        }
        List<Long> orderItemIds = new ArrayList<>();
        for(OrderItemDto orderItem :orderItemDto){
            Optional<Product> optProduct = productRepository.findById(orderItem.getProductId());

            if(!optProduct.isPresent()){
                return ResponseEntity.ok().body(false);
            }
            log.info("프로덕트 조회 : "+ optProduct.get());

            Long sellId = optProduct.get().getSeller().getId();

            log.info("셀러 아이디 가지고 왔나요? : "+sellId);

            Optional<Seller> optSeller = sellerRepository.findById(sellId);

            if(!optSeller.isPresent()){
                return ResponseEntity.ok().body(false);
            }
            log.info("셀러 조회: "+optSeller.get());

            OrderItem savedorderItem = OrderItem.builder()
                    .order(order)
                    .product(optProduct.get())
                    .seller(optSeller.get())
                    .quantity(orderItem.getQuantity())
                    .total(orderItem.getTotalPrice())
                    .discount(orderItem.getDiscount())
                    .deli(orderItem.getDeliver())
                    .build();

            log.info("오더아이템디티오 엔티티로 변환 : "+savedorderItem);

            OrderItem returnorderItem= orderItemRepository.save(savedorderItem);
            Long orderItemId = returnorderItem.getId();
            log.info("오더아이템 저장 성공 : "+returnorderItem);

            orderItemIds.add(orderItemId);

            if(returnorderItem==null){
                return ResponseEntity.ok().body(false);
            }
        };

        session.setAttribute("orderItemIds",orderItemIds);
        return ResponseEntity.ok().body(true);
    }

    public ResponseOrderDto selectedOrderComplete(List<Long> orderItemIds) {

        log.info("오더아이템 컨트롤러에 들어오는지 확인 "+orderItemIds);
        List<OrderItem> orderItems = orderItemRepositoryImpl.selectOrderItemsByOrderId(orderItemIds);
        log.info("오더아이템 임플 결과 "+orderItems);

        List<ResponseOrderItemDto> orderItemDtos = new ArrayList<>();
        for(OrderItem orderItem : orderItems){

            ResponseOrderItemDto responseOrderItemDto = ResponseOrderItemDto.builder()
                                                                        .prodListImg(orderItem.getProduct().getProdListImg())
                                                                        .prodName(orderItem.getProduct().getProdName())
                                                                        .prodSummary(orderItem.getProduct().getProdSummary())
                                                                        .prodPrice((int)Math.round(orderItem.getProduct().getProdPrice()))
                                                                        .discount((int)Math.round(orderItem.getProduct().getProdPrice()*(orderItem.getDiscount()/100)))
                                                                        .quantity(orderItem.getQuantity())
                                                                        .totalPrice((int)Math.round(orderItem.getProduct().getProdPrice()))
                                                                        .build();
            orderItemDtos.add(responseOrderItemDto);
        }
        Order order = orderItems.get(0).getOrder();

        ResponseOrderDto responseOrderDto = ResponseOrderDto.builder()
                .orderId(order.getId())
                .custName(order.getCustomer().getCustName())
                .custHp(order.getCustomer().getCustHp())
                .OrderTotal(order.getOrderTotal())
                .receiverName(order.getReceiverName())
                .receiverHp(order.getReceiverHp())
                .receiverAddr1(order.getReceiverAddr())
                .orderItemDtos(orderItemDtos)
                .build();

        return responseOrderDto;
    }

    public ResponseOrderDto selectAdminOrder(Long orderId) {

        MyUserDetails auth =(MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = auth.getUser().getMemRole();
        Seller seller = auth.getUser().getSeller();
        Long memId = auth.getUser().getId();

        log.info("셀러인지 어드민인지 확인하기 "+role+memId);

        List<OrderItem> orderItems = new ArrayList<>();

        if(role.equals("admin")){
            orderItems = orderItemRepository.findAllByOrder_Id(orderId);
        }else if(role.equals("seller")){
            orderItems = orderItemRepository.findAllBySellerAndOrder_Id(seller,orderId);
            log.info("오더 조회 잘 되었는지 확인!! "+orderItems.size());
        }



        List<ResponseOrderItemDto> orderItemDtos = new ArrayList<>();
        for(OrderItem orderItem : orderItems){
            ResponseOrderItemDto orderItemDto = ResponseOrderItemDto.builder()
                                                        .orderItemId(orderItem.getId())
                                                        .prodListImg(orderItem.getProduct().getProdListImg())
                                                        .prodName(orderItem.getProduct().getProdName())
                                                        .prodId(orderItem.getProduct().getId())
                                                        .sellerName(orderItem.getSeller().getSellCompany())
                                                        .prodPrice((int)Math.round(orderItem.getProduct().getProdPrice()))
                                                        .discount((int)Math.round(orderItem.getProduct().getProdPrice()*(orderItem.getDiscount()/100)))
                                                        .quantity(orderItem.getQuantity())
                                                        .delivery(orderItem.getDeli())
                                                        .orderDeliId(orderItem.getOrderDeliId()==null?"":orderItem.getOrderDeliId())
                                                        .totalPrice((int)Math.round(orderItem.getProduct().getProdPrice()))
                                                        .build();

            orderItemDtos.add(orderItemDto);
        }
        String[] addr = orderItems.get(0).getOrder().getReceiverAddr().split("/");
        return ResponseOrderDto.builder()
                .orderId(orderItems.get(0).getOrder().getId())
                .payment(orderItems.get(0).getOrder().getOrderPayment())
                .custName(orderItems.get(0).getOrder().getCustomer().getCustName())
                .custHp(orderItems.get(0).getOrder().getCustomer().getCustHp())
                .orderState(orderItems.get(0).getOrder().getOrderState())
                .receiverName(orderItems.get(0).getOrder().getReceiverName())
                .receiverHp(orderItems.get(0).getOrder().getReceiverHp())
                .receiverAddr1(addr[0])
                .receiverAddr2(addr[1])
                .receiverAddr3(addr[2])
                .orderReq(
                        (orderItems.get(0).getOrder().getOrderReq() != null && !orderItems.get(0).getOrder().getOrderReq().isEmpty())
                                ? orderItems.get(0).getOrder().getOrderReq()
                                : ""
                )
                .orderItemDtos(orderItemDtos)
                .build();
    }
}
