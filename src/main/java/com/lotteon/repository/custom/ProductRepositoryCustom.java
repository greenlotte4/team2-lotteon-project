package com.lotteon.repository.custom;

import com.lotteon.dto.requestDto.ProductPageRequestDTO;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    Page<Tuple> selectProductAllForList(ProductPageRequestDTO pageRequestDTO, Pageable pageable, long sellId);
    Page<Tuple> selectProductForSearch(ProductPageRequestDTO pageRequestDTO, Pageable pageable, long sellId);

    List<Tuple> findProductsWithSellerInfoByIds(List<Long> productIds);

}
