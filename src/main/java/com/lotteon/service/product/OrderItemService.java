package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.GetDeliveryDto;
import com.lotteon.dto.requestDto.cartOrder.OrderDto;
import com.lotteon.dto.requestDto.cartOrder.OrderItemDto;
import com.lotteon.dto.responseDto.GetAdminOrderNameDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderItemDto;
import com.lotteon.dto.responseDto.cartOrder.UserOrderDto;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.point.Point;
import com.lotteon.entity.product.Order;
import com.lotteon.entity.product.OrderItem;
import com.lotteon.entity.product.Product;
import com.lotteon.entity.product.ProductOption;
import com.lotteon.repository.impl.OrderItemRepositoryImpl;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.SellerRepository;
import com.lotteon.repository.point.PointRepository;
import com.lotteon.repository.product.OrderItemRepository;
import com.lotteon.repository.product.OrderRepository;
import com.lotteon.repository.product.ProductOptionRepository;
import com.lotteon.repository.product.ProductRepository;
import com.lotteon.service.member.CustomerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final PointRepository pointRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final ProductOptionRepository productOptionRepository;

    //주문수 +1
    public void ProductOrderCount(Product product) {
        product.setProdOrderCnt(product.getProdOrderCnt() + 1);
    }

    public ResponseEntity insertOrderItem(List<OrderItemDto> orderItemDto, OrderDto orderDto, HttpSession session) {
        log.info("오더아이템 서비스 들어옴 ");
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        LocalDate today = LocalDate.now();
        Order order = orderService.insertOrder(orderDto);
        log.info("오더 저장 성공 : "+order);

        if(order==null){
            ResponseEntity.badRequest().body("로그인 해야 함");
        }
        List<Long> orderItemIds = new ArrayList<>();
        for(OrderItemDto orderItem :orderItemDto){
            Optional<Product> optProduct = productRepository.findById(orderItem.getProductId());
            if(optProduct.isPresent()){
                ProductOrderCount(optProduct.get());
            }

            int orderItemQuantity = orderItem.getQuantity();
            Long optionId = orderItem.getOptionId();

            //재고 처리
            Optional<ProductOption> optProductOption = productOptionRepository.findById(optionId);
            ProductOption productOption = optProductOption.get();
            int savedStock = productOption.getStock() - orderItemQuantity;
            productOption.setStock(savedStock);

            if(!optProduct.isPresent()){
                return ResponseEntity.ok().body(false);
            }
            log.info("프로덕트 조회 : "+ optProduct.get());

            Long sellId = optProduct.get().getSeller().getId();
            Seller seller = optProduct.get().getSeller();
            System.out.println("1번 셀러"+seller);
            log.info("셀러 아이디 가지고 왔나요? : "+sellId);

            Optional<Seller> optSeller = sellerRepository.findById(sellId);
            System.out.println("2번 셀러"+optSeller.get());
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
                    .optionId(orderItem.getOptionId())
                    .build();

            log.info("오더아이템디티오 엔티티로 변환 : "+savedorderItem);

            OrderItem returnorderItem= orderItemRepository.save(savedorderItem);
            Long orderItemId = returnorderItem.getId();
            log.info("오더아이템 저장 성공 : "+returnorderItem);

            orderItemIds.add(orderItemId);

            Point point = Point.builder()
                    .pointType(1)
                    .pointVar(orderItem.getSavePoint()*orderItem.getQuantity())
                    .customer(customer)
                    .pointEtc("상품구매 포인트적립")
                    .pointExpiration(today.plusMonths(2))
                    .orderId(orderItemId)
                    .build();

            pointRepository.save(point);

            int points = customerService.updateCustomerPoint(customer);
            customer.updatePoint(points);
            customerRepository.save(customer);

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
                                                                        .delivery(orderItem.getDeli())
                                                                        .prodPoint(orderItem.getProduct().getProdPoint())
                                                                        .optionId(orderItem.getOptionId())
                                                                        .totalPrice((int)Math.round(orderItem.getTotal()))
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
                .payment(order.getOrderPayment())
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
            Long optionId = orderItem.getOptionId();
            List<String> options = findOptions(optionId);
            ResponseOrderItemDto orderItemDto = ResponseOrderItemDtoBuilder(orderItem,options);
            orderItemDtos.add(orderItemDto);

            orderItemDtos.add(orderItemDto);
        }
        return ResponseOrderDtoBuilder(orderItems, orderItemDtos);
    }

    public List<GetAdminOrderNameDto> selectAdminOrderItem(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(orderId);
        List<GetAdminOrderNameDto> dtos = orderItems.stream().map(v->v.toGetAdminOrderNameDto()).toList();
        return dtos;
    }

    public Page<GetDeliveryDto> findAllBySeller(int page) {
        Pageable pageable = PageRequest.of(page,10);
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller = auth.getUser().getSeller();
        Page<OrderItem> orderItems = orderItemRepository.findAllBySellerAndOrderDeliIdIsNotNullAndOrderDeliCompanyNotNullOrderByDeliSdateDesc(seller,pageable);
        Page<GetDeliveryDto> dtos = orderItems.map(OrderItem::toGetDeliveryDto);
        return dtos;
    }

    public Page<GetDeliveryDto> findAllBySellerAndSearchType(int page, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC, "id"));
        Page<OrderItem> orders;
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller = auth.getUser().getSeller();
        if(searchType.equals("deliId")){
            orders = orderItemRepository.findAllBySellerAndOrderDeliIdAndOrderDeliCompanyNotNullOrderByDeliSdateDesc(seller,keyword,pageable);
        } else if (searchType.equals("orderId")){
            orders = orderItemRepository.findAllBySellerAndIdAndOrderDeliIdIsNotNullAndOrderDeliCompanyNotNullOrderByDeliSdateDesc(seller,Long.parseLong(keyword),pageable);
        } else {
            orders = orderItemRepository.findAllBySellerAndOrder_ReceiverNameAndOrderDeliIdIsNotNullAndOrderDeliCompanyNotNullOrderByDeliSdateDesc(seller,keyword,pageable);
        }
        Page<GetDeliveryDto> dtos = orders.map(OrderItem::toGetDeliveryDto);
        return dtos;
    }

    public ResponseOrderDto selectMyOrderInfo(Long orderId) {
        log.info("서비스 orderId : "+orderId);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(orderId);
        log.info("서비스 오더 아이템 뽑은 거 "+orderItems.toString());

        List<ResponseOrderItemDto> orderItemDtos = new ArrayList<>();
        for(OrderItem orderItem : orderItems){
            Long optionId = orderItem.getOptionId();
            List<String> options = findOptions(optionId);
            ResponseOrderItemDto orderItemDto = ResponseOrderItemDtoBuilder(orderItem, options);
            orderItemDtos.add(orderItemDto);
        }
        ResponseOrderDto dtos = ResponseOrderDtoBuilder(orderItems, orderItemDtos);

        log.info("마이인포 주문상세 뽑은 데이터 확인 "+dtos.toString());
        return dtos;
    }

    //toDto 메서드
    public ResponseOrderDto ResponseOrderDtoBuilder(List<OrderItem> orderItems, List<ResponseOrderItemDto> orderItemDtos) {
        String[] addr = orderItems.get(0).getOrder().getReceiverAddr().split("/");
        return
                ResponseOrderDto.builder()
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
                                        : null
                        )
                        .orderItemDtos(orderItemDtos)
                        .build();
    }
    public ResponseOrderItemDto ResponseOrderItemDtoBuilder(OrderItem orderItem, List<String> options) {
        return ResponseOrderItemDto.builder()
                .orderItemId(orderItem.getId())
                .prodListImg(orderItem.getProduct().getProdListImg())
                .prodName(orderItem.getProduct().getProdName())
                .prodId(orderItem.getProduct().getId())
                .sellerName(orderItem.getSeller().getSellCompany())
                .prodPrice((int)Math.round(orderItem.getProduct().getProdPrice()))
                .discount((int)Math.round(Double.valueOf(orderItem.getDiscount())/100*orderItem.getProduct().getProdPrice()))
                .quantity(orderItem.getQuantity())
                .delivery(orderItem.getDeli())
                .orderDeliCompany(orderItem.getOrderDeliCompany()==null?0:orderItem.getOrderDeliCompany())
                .orderDeliId(orderItem.getOrderDeliId()==null?"":orderItem.getOrderDeliId())
                .totalPrice((int)Math.round(orderItem.getTotal()))
                .orderItemState2(orderItem.getState2())
                .options(options)
                .build();
    }
    public List<String> findOptions(Long optionId){
        List<String> optionValue = new ArrayList<>();
        if(optionId!=null){
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
        return optionValue;
    }

}