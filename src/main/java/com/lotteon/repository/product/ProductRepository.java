package com.lotteon.repository.product;

import com.lotteon.dto.requestDto.PostProductDTO;
import com.lotteon.entity.product.Product;
import com.lotteon.repository.custom.ProductRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    public Page<Product> findAllBySellId(Pageable pageable, Long sellId);
    List<Product> findAllByIdIn(List<Long> ids);
}
