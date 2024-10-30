package com.lotteon.dto.responseDto.cartOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/*
    날짜: 2024/10/29
    이름: 박연화
    내용: 관리자 주문현황 주문상세 구현을 위해 필드 추가(2024/10/29)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderItemDto{
    private Long orderItemId;
    private String prodListImg;
    private String prodName;
    private String prodSummary;
    private int prodPrice;
    private int discount;
    private int quantity;
    private int totalPrice;

    //추가필드
    private Long prodId;
    private int delivery;
    private String sellerName;
    private String orderDeliId;
}
