package com.lotteon.dto.responseDto.cartOrder;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCartDto {
    private Long cartItemId;
    private int quantity;
    private double totalPrice;
    private Long optionId;
    private List<String> optionValue;
//    private String optionValue2;
//    private String optionValue3;
    private CartProductDto cartProductDto;
    private int additionalPrice;
}
