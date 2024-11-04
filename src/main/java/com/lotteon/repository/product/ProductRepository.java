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

    List<Product> findAllByIdIn(List<Long> ids);

    Page<Product> findAllByProdNameContainingOrderByProdViewsDesc(String search, Pageable pageable);

    Page<Product> findAllByProdNameContainingOrderByProdOrderCntDesc(String search, Pageable pageable);

    Page<Product> findAllByOrderByProdOrderCntDesc(Pageable pageable);

    Page<Product> findAllByProdNameContainingOrderByProdPriceDesc(String search, Pageable pageable);

    Page<Product> findAllByProdNameContainingOrderByProdPriceAsc(String search, Pageable pageable);

    Page<Product> findAllByProdNameContainingOrderByProdRatingDesc(String search, Pageable pageable);

    Page<Product> findAllByProdNameContainingOrderByProdReviewCntDesc(String search, Pageable pageable);

    Page<Product> findAllByProdNameContainingOrderByProdRdateDesc(String search, Pageable pageable);

    Page<Product> findAllByOrderByProdRdateDesc(Pageable pageable);

    Page<Product> findAllByOrderByProdReviewCntDesc(Pageable pageable);

    Page<Product> findAllByOrderByProdRatingDesc(Pageable pageable);

    Page<Product> findAllByOrderByProdPriceAsc(Pageable pageable);

    Page<Product> findAllByOrderByProdPriceDesc(Pageable pageable);

    List<Product> findTop3ByOrderByProdOrderCntDesc();

    List<Product> findTop4ByOrderByProdViewsDesc();

    List<Product> findTop4ByOrderByProdRdateDesc();

    List<Product> findTop4ByOrderByProdRatingDesc();

    List<Product> findTop4ByOrderByProdDiscountDesc();

    List<Product> findTop4ByOrderByProdPointDesc();
}
