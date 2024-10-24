package com.lotteon.dto.responseDto;

import com.lotteon.dto.responseDto.cartOrder.CartItemDto;
import com.lotteon.dto.responseDto.cartOrder.CartItemOptionDto;
import com.lotteon.dto.responseDto.cartOrder.ProductDto;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderDto {
    private ProductDto products;
    private CartItemDto cartItems;
    private CartItemOptionDto cartItemOptions;
}
