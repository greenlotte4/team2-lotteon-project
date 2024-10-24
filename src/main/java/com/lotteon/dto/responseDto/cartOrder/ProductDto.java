package com.lotteon.dto.responseDto.cartOrder;

import com.lotteon.entity.category.CategoryProductMapper;
import com.lotteon.entity.product.ProductOption;
import com.lotteon.entity.product.ProductStock;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    private List<ProductStock> stocks;

}
