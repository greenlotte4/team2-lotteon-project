package com.lotteon.dto.responseDto.cartOrder;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private Long id;
    private Long sellId;
    private String prodSummary;
    private Double prodPrice;
    private Double prodDiscount;
    private int prodDeliver;
    private String prodName;
    private String prodListImg;
    private int prodPoint;
    private int stock;
    private int totalPrice;
}
