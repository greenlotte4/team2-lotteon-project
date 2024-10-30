package com.lotteon.dto.requestDto.cartOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostOrderDeliDto {
    private Long orderId;
    private int orderDeli;
    private int orderState;
    private String orderDeliId;
}
