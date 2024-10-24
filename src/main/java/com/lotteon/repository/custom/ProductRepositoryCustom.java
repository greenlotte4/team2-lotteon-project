package com.lotteon.repository.custom;

import com.lotteon.dto.requestDto.ProductPageRequestDTO;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    public Page<Tuple> selectArticleAllForList(ProductPageRequestDTO pageRequestDTO, Pageable pageable, long sellId);
    public Page<Tuple> selectArticleForSearch(ProductPageRequestDTO pageRequestDTO, Pageable pageable, long sellId);



}
