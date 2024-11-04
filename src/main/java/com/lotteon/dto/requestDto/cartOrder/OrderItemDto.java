package com.lotteon.dto.requestDto.cartOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private int quantity;
    private Long productId;
    private int totalPrice;
    private int discount;
    private int deliver;
    private int productPrice;
    private int savePoint;
    private Long optionId;
}
