package com.lotteon.dto.responseDto.cartOrder;

import com.lotteon.dto.requestDto.cartOrder.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
/*
    날짜: 2024/10/27
    이름: 박연화
    내용: responseOrderDto 생성

    수정이력
    - 2024/10/29 - 박연화 관리자 주문현황 주문상세 페이지에 출력될 필드 추가
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseOrderDto {
    private Long orderId;
    private String custName;
    private String custHp;
    private String receiverName;
    private String receiverHp;
    private String receiverAddr1;
    private String receiverAddr2;
    private String receiverAddr3;
    private int OrderTotal;
    private List<ResponseOrderItemDto> orderItemDtos;

    //추가필드
    private int payment;
    private int orderState;
    private String orderReq;

}
