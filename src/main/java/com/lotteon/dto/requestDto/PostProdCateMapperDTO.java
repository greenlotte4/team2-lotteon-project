package com.lotteon.dto.requestDto;

import com.lotteon.entity.category.CategoryProduct;
import com.lotteon.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostProdCateMapperDTO {
    private Long id;
    private Product product;
    private CategoryProduct category;
}

