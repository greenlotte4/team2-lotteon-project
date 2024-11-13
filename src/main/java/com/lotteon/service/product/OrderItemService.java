package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.GetDeliveryDto;
import com.lotteon.dto.requestDto.cartOrder.OrderDto;
import com.lotteon.dto.requestDto.cartOrder.OrderItemDto;
import com.lotteon.dto.requestDto.cartOrder.OrderPointAndCouponDto;
import com.lotteon.dto.responseDto.GetAdminOrderNameDto;
import com.lotteon.dto.responseDto.GetDeliveryDateDto;
import com.lotteon.dto.responseDto.GetReceiveConfirmDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderItemDto;
import com.lotteon.dto.responseDto.cartOrder.UserOrderDto;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.point.Point;
import com.lotteon.entity.product.*;
import com.lotteon.repository.impl.OrderItemRepositoryImpl;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.SellerRepository;
import com.lotteon.repository.point.PointRepository;
import com.lotteon.repository.product.*;
import com.lotteon.service.WebSocketService;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private final OrderRepository orderRepository;
    private final OrderCancleRepository orderCancleRepository;
    private final ProductService productService;
    private final WebSocketService webSocketService;

    //주문수 +1
    public void ProductOrderCount(Product product) {
        product.setProdOrderCnt(product.getProdOrderCnt() + 1);
    }

    public ResponseEntity<Map<String,Object>> insertOrderItem(List<OrderItemDto> orderItemDto, OrderDto orderDto, HttpSession session, OrderPointAndCouponDto dto) {
        log.info("오더아이템 서비스 들어옴 ");
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        LocalDate today = LocalDate.now();
        Order order = orderService.insertOrder(orderDto);
        Map<String,Object> map = new HashMap<>();
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

            String updateResult = productService.top3UpdateBoolean();
            if(updateResult.equals("true")){
                productService.updateBestItems();
            }
            //재고 처리
            Optional<ProductOption> optProductOption = productOptionRepository.findById(optionId);
            ProductOption productOption = optProductOption.get();
            int savedStock = productOption.getStock() - orderItemQuantity;
            productOption.setStock(savedStock);

            if(!optProduct.isPresent()){
                map.put("status",false);
                return ResponseEntity.ok().body(map);
            }
            log.info("프로덕트 조회 : "+ optProduct.get());

            Long sellId = optProduct.get().getSeller().getId();
            Seller seller = optProduct.get().getSeller();
            System.out.println("1번 셀러"+seller);
            log.info("셀러 아이디 가지고 왔나요? : "+sellId);

            Optional<Seller> optSeller = sellerRepository.findById(sellId);
            System.out.println("2번 셀러"+optSeller.get());
            if(!optSeller.isPresent()){
                map.put("status",false);
                return ResponseEntity.ok().body(map);
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
                    .orderId(returnorderItem.getId())
                    .build();

            pointRepository.save(point);
            map.put("pointId",point.getId());
            int points = customerService.updateCustomerPoint(customer);
            customer.updatePoint(points);
            customerRepository.save(customer);

            OrderCancleDocument orderCancleDocument = OrderCancleDocument.builder()
                    .pointId(point.getId())
                    .orderItemId(returnorderItem.getId())
                    .custId(customer.getId())
                    .pointUdate(LocalDateTime.now())
                    .points(orderItem.getSavePoint()*orderItem.getQuantity())
                    .orderId(order.getId())
                    .couponId(dto.getCouponId())
                    .build();

            orderCancleRepository.save(orderCancleDocument);

            if(returnorderItem==null){
                map.put("status",false);
                return ResponseEntity.ok().body(map);
            }
        };
        map.put("orderId", order.getId());
        map.put("status",true);

        session.setAttribute("orderItemIds",orderItemIds);

        return ResponseEntity.ok().body(map);
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
            Map<String, Object> optionResult = findOptions(optionId);
            Integer additionalPrice = (Integer) optionResult.get("additionalPrice");
            List<String> options = (List<String>) optionResult.get("optionValues");
            ResponseOrderItemDto orderItemDto = ResponseOrderItemDtoBuilder(orderItem,options,additionalPrice);
            orderItemDtos.add(orderItemDto);
        }
        return ResponseOrderDtoBuilder(orderItems, orderItemDtos);
    }

    public List<GetAdminOrderNameDto> selectAdminOrderItem(Long orderId) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller = auth.getUser().getSeller();
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_IdAndSeller(orderId,seller);
        List<GetAdminOrderNameDto> dtos = orderItems.stream().map(OrderItem::toGetAdminOrderNameDto).toList();
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
            Map<String, Object> optionResult = findOptions(optionId);
            Integer additionalPrice = (Integer) optionResult.get("additionalPrice");
            List<String> options = (List<String>) optionResult.get("optionValues");
            ResponseOrderItemDto orderItemDto = ResponseOrderItemDtoBuilder(orderItem, options,additionalPrice);
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
                        .orderDate(new SimpleDateFormat("(yyyy.MM.dd)").format(orderItems.get(0).getOrder().getOrderRdate()))
                        .receiverName(orderItems.get(0).getOrder().getReceiverName())
                        .receiverHp(orderItems.get(0).getOrder().getReceiverHp())
                        .receiverAddr1(addr[0])
                        .receiverAddr2(addr[1])
                        .receiverAddr3(addr[2])
                        .OrderTotal(orderItems.get(0).getOrder().getOrderTotal())
                        .orderReq(
                                (orderItems.get(0).getOrder().getOrderReq() != null && !orderItems.get(0).getOrder().getOrderReq().isEmpty())
                                        ? orderItems.get(0).getOrder().getOrderReq()
                                        : null
                        )
                        .orderItemDtos(orderItemDtos)
                        .build();
    }
    public ResponseOrderItemDto ResponseOrderItemDtoBuilder(OrderItem orderItem, List<String> options, int additionalPrice) {
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
                .additionalPrice(additionalPrice)
                .options(options)
                .build();
    }
    public Map<String, Object> findOptions(Long optionId){
        List<String> optionValue = new ArrayList<>();
        Integer additionalPrice = null;
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
            if(option.getAdditionalPrice()!=null){
                additionalPrice = (int) Math.round(option.getAdditionalPrice());
            }
        }
        log.info(" 옵션 밸류 볼래용 "+optionValue.toString());

        Map<String, Object> result = new HashMap<>();
        result.put("optionValues", optionValue);
        result.put("additionalPrice", additionalPrice);

        return result;
    }

    public List<GetDeliveryDateDto> findDeliveryDateAllByOrderId(Long id) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(id);
        List<GetDeliveryDateDto> dtos = orderItems.stream().map(OrderItem::toGetDeliveryDateDto).toList();
        return dtos;
    }

    public List<GetReceiveConfirmDto> findReceiveAllByOrderId(Long id) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_IdAndState2(id,4);
        List<GetReceiveConfirmDto> dtos = orderItems.stream().map(OrderItem::toGetReceiveConfirmDto).toList();
        return dtos;
    }

    public void patchItemState(Long id, int itemState2, int itemState1, int orderState) {
        Optional<OrderItem> orderItem = orderItemRepository.findById(id);
        orderItem.get().updateState2(itemState2);
        orderItem.get().updateState1(itemState1);
        orderItem.get().getOrder().updateState(orderState);
        orderItemRepository.save(orderItem.get());
        orderRepository.save(orderItem.get().getOrder());


        Optional<Point> point = pointRepository.findById(id);
        if(point.isPresent()){
            pointRepository.delete(point.get());
        }

        int totalCnt = orderItem.get().getOrder().getOrderItems().size();
        int variableCnt = 0;
        List<OrderItem> orderItems = orderItem.get().getOrder().getOrderItems();
        Long orderId = orderItem.get().getOrder().getId();
        Optional<Order> order = orderRepository.findById(orderId);
        List<OrderItem> orderItemList = order.get().getOrderItems();
        for(OrderItem item : orderItemList){
            if(item.getState1()==1){
                variableCnt++;
            }
        }

        if(totalCnt == variableCnt){
            order.get().updateState(4);
            orderRepository.save(order.get());
        }
    }

    public void patchCancelState(Long id, int itemState2, int itemState1, int orderState) {
        Optional<Order> order = orderRepository.findById(id);
        order.get().updateState(orderState);

        for(OrderItem orderItem : order.get().getOrderItems()){
            orderItem.updateState2(itemState2);
            orderItem.updateState1(itemState1);
            log.info("서비스 확인 "+orderItem.toString());
            orderItemRepository.save(orderItem);
            prodCountUp(orderItem);
            prodOrderCountDown(orderItem);
        }
    }

    public void prodCountUp(OrderItem orderItem) {
        Long optionId = orderItem.getOptionId();
        Optional<ProductOption> productOption = productOptionRepository.findById(optionId);

        int qty = orderItem.getQuantity();
        int stock = productOption.get().getStock();
        int editStock = stock + qty;

        productOption.get().setStock(editStock);
    }

    public void prodOrderCountDown(OrderItem orderItem) {
        Long prodId = orderItem.getProduct().getId();
        Optional<Product> optProduct = productRepository.findById(prodId);
        optProduct.get().setProdOrderCnt(optProduct.get().getProdOrderCnt()-1);
    }

    public Long findItemCnt(int i,LocalDateTime startOfDay, LocalDateTime endOfDay) {
        Timestamp startTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endTimestamp = Timestamp.valueOf(endOfDay);
        Long cnt = orderItemRepository.countByState2AndOrder_OrderRdateBetween(i,startTimestamp,endTimestamp);
        return cnt;
    }

    public Long findAllItemCnt(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        Timestamp startTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endTimestamp = Timestamp.valueOf(endOfDay);
        Long cnt = orderItemRepository.countByOrder_OrderRdateBetween(startTimestamp,endTimestamp);
        return cnt;
    }

    public int findTotalPrice(LocalDateTime startOfDay,LocalDateTime endOfDay) {
        Timestamp startTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endTimestamp = Timestamp.valueOf(endOfDay);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_OrderRdateBetween(startTimestamp,endTimestamp);
        int totalPrice =0;
        for(OrderItem item : orderItems){
            totalPrice += item.getTotal();
        }

        return totalPrice;
    }

    public List<Long> findItemOrder(LocalDateTime startDay) {
        LocalDateTime endDay = startDay.withHour(23).withMinute(59).withSecond(59);
        List<Long> cnts = new ArrayList<>();
        cnts.add(orderItemRepository.countByOrder_OrderRdateBetween(Timestamp.valueOf(startDay.minusDays(4)),Timestamp.valueOf(endDay.minusDays(4))));
        cnts.add(orderItemRepository.countByOrder_OrderRdateBetween(Timestamp.valueOf(startDay.minusDays(3)),Timestamp.valueOf(endDay.minusDays(3))));
        cnts.add(orderItemRepository.countByOrder_OrderRdateBetween(Timestamp.valueOf(startDay.minusDays(2)),Timestamp.valueOf(endDay.minusDays(2))));
        cnts.add(orderItemRepository.countByOrder_OrderRdateBetween(Timestamp.valueOf(startDay.minusDays(1)),Timestamp.valueOf(endDay.minusDays(1))));
        cnts.add(orderItemRepository.countByOrder_OrderRdateBetween(Timestamp.valueOf(startDay),Timestamp.valueOf(endDay)));
        return cnts;
    }

    public List<Long> findItemType(LocalDateTime startDay,int type) {
        LocalDateTime endDay = startDay.withHour(23).withMinute(59).withSecond(59);
        List<Long> cnts = new ArrayList<>();
        cnts.add(orderItemRepository.countByState2AndOrder_OrderRdateBetween(type,Timestamp.valueOf(startDay.minusDays(4)),Timestamp.valueOf(endDay.minusDays(4))));
        cnts.add(orderItemRepository.countByState2AndOrder_OrderRdateBetween(type,Timestamp.valueOf(startDay.minusDays(3)),Timestamp.valueOf(endDay.minusDays(3))));
        cnts.add(orderItemRepository.countByState2AndOrder_OrderRdateBetween(type,Timestamp.valueOf(startDay.minusDays(2)),Timestamp.valueOf(endDay.minusDays(2))));
        cnts.add(orderItemRepository.countByState2AndOrder_OrderRdateBetween(type,Timestamp.valueOf(startDay.minusDays(1)),Timestamp.valueOf(endDay.minusDays(1))));
        cnts.add(orderItemRepository.countByState2AndOrder_OrderRdateBetween(type,Timestamp.valueOf(startDay),Timestamp.valueOf(endDay)));
        return cnts;
    }

    public List<Integer> findItemTotalPriceByCategory(LocalDateTime starDay, LocalDateTime endDay) {
        List<OrderItem> orderItems1 = orderItemRepository.findAllByProduct_CategoryMappings_Category_CategoryIdAndOrder_OrderRdateBetween((long)1,Timestamp.valueOf(starDay),Timestamp.valueOf(endDay));
        int totalPrice1 =0;
        for(OrderItem item : orderItems1){
            totalPrice1 += item.getTotal();
        }

        List<OrderItem> orderItems2 = orderItemRepository.findAllByProduct_CategoryMappings_Category_CategoryIdAndOrder_OrderRdateBetween((long)55,Timestamp.valueOf(starDay),Timestamp.valueOf(endDay));
        int totalPrice2 =0;
        for(OrderItem item : orderItems2){
            totalPrice2 += item.getTotal();
        }

        List<OrderItem> orderItems3 = orderItemRepository.findAllByProduct_CategoryMappings_Category_CategoryIdAndOrder_OrderRdateBetween((long)151,Timestamp.valueOf(starDay),Timestamp.valueOf(endDay));
        int totalPrice3 =0;
        for(OrderItem item : orderItems3){
            totalPrice3 += item.getTotal();
        }

        List<OrderItem> orderItems4 = orderItemRepository.findAllByProduct_CategoryMappings_Category_CategoryIdAndOrder_OrderRdateBetween((long)177,Timestamp.valueOf(starDay),Timestamp.valueOf(endDay));
        int totalPrice4 =0;
        for(OrderItem item : orderItems4){
            totalPrice4 += item.getTotal();
        }

        List<Integer> totalPrices = new ArrayList<>();
        totalPrices.add(totalPrice1);
        totalPrices.add(totalPrice2);
        totalPrices.add(totalPrice3);
        totalPrices.add(totalPrice4);

        return totalPrices;
    }
}

