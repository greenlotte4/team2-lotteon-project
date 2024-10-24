package com.lotteon.dto.responseDto.cartOrder;

import com.lotteon.entity.product.Cart;
import com.lotteon.entity.product.CartItemOption;
import com.lotteon.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

    private Long id;

    private Cart cart;

    private int quantity;

}
