package com.lotteon.dto.responseDto.cartOrder;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderDto {
    private ProductDto products;
    private CartItemDto cartItems;
    private int quantity;
    private Long optionId;
    private List<String> optionValue;
    private String option2;
    private String option3;
    private int totalPrice;
}
