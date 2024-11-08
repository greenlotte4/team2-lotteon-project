package com.lotteon.controller.apicontroller;

import com.lotteon.dto.requestDto.MyInfoPassDto;
import com.lotteon.dto.responseDto.GetDeliveryDateDto;
import com.lotteon.dto.responseDto.GetReceiveConfirmDto;
import com.lotteon.dto.responseDto.GetSellerInfoDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderDto;
import com.lotteon.service.member.SellerService;
import com.lotteon.service.product.OrderItemService;
import com.lotteon.service.product.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;



import org.springframework.web.bind.annotation.*;
import java.util.List;



import org.springframework.web.bind.annotation.*;
import java.util.List;
@Log4j2
@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class ApiMyOrderController {

    private final SellerService sellerService;
    private final OrderItemService orderItemService;
    private final OrderService orderService;

    @GetMapping("/order/seller-info")
    public ResponseEntity<?> sellerInfo(
            @RequestParam String company
    ) {
        GetSellerInfoDto seller = sellerService.findBySellerCompany(company);
        return ResponseEntity.ok(seller);
    }


    @GetMapping("/order/orderInfo")
    public ResponseEntity<?> orderInfo(
            @RequestParam Long orderId
    ) {
        log.info("orderId: " + orderId);
        ResponseOrderDto dtos  = orderItemService.selectMyOrderInfo(orderId);

        return ResponseEntity.ok(dtos);
    }
  

    @GetMapping("/order/delivery-date")
    public ResponseEntity<?> deliveryDate(
            @RequestParam Long id
    ){
        List<GetDeliveryDateDto> deliveryDate = orderItemService.findDeliveryDateAllByOrderId(id);

        return ResponseEntity.ok(deliveryDate);
    }

    @GetMapping("/order/receive")
    public ResponseEntity<?> receive(
            @RequestParam Long id
    ){
        List<GetReceiveConfirmDto> confirms = orderItemService.findReceiveAllByOrderId(id);
        return ResponseEntity.ok(confirms);
    }

    @PatchMapping("/order/receive")
    public ResponseEntity<?> patchReceive(
            @RequestParam Long id
    ){
        orderItemService.patchItemState(id,5,1,3);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/order/return")
    public ResponseEntity<?> patchReturn(
            @RequestParam Long id
    ){
        orderItemService.patchItemState(id,2,1,3);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/order/change")
    public ResponseEntity<?> patchChange(
            @RequestParam Long id
    ){
        orderItemService.patchItemState(id,3,1,3);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/order/cancel")
    public ResponseEntity<?> orderCancel(
            @RequestParam Long id
    ){
        log.info("주문취소 컨트롤러 id: " + id);
        orderItemService.patchCancelState(id,6,0,5);
        orderService.cancleOrder(id);
        return ResponseEntity.ok().build();
    }

}
