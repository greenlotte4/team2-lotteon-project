package com.lotteon.repository.category;

import com.lotteon.entity.category.CategoryProduct;
import com.lotteon.entity.category.CategoryProductMapper;
import com.lotteon.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryProdMapperRepository extends JpaRepository<CategoryProductMapper,Long> {
    List<CategoryProductMapper> findAllByCategory(CategoryProduct cate);

    List<CategoryProductMapper> findAllByProduct(Product prod);

}
