package com.lotteon.repository.product;

import com.lotteon.dto.requestDto.PostProdDetailDTO;
import com.lotteon.entity.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    ProductDetail findByProductId(long id);
}
