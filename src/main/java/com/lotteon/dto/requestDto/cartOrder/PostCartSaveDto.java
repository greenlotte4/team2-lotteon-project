package com.lotteon.dto.requestDto.cartOrder;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCartSaveDto {

    private Long cartItemId;
    private Long productId;
    private int quantity;
    private Long optionId;
    private int totalPrice;

}
