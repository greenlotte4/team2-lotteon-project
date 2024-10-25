package com.lotteon.repository.category;

import com.lotteon.entity.category.CategoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryProductRepository extends JpaRepository<CategoryProduct, Long> {
    List<CategoryProduct> findAllByParent(CategoryProduct categoryProduct);

    List<CategoryProduct> findByParent(CategoryProduct categoryProduct);

    List<CategoryProduct> findAllByCategoryLevel(int i);

    List<CategoryProduct> findAllByCategoryId(Long id);

    CategoryProduct findByCategoryId(Long id);

    CategoryProduct findByCategoryIdOrderByCategoryOrder(Long id);

    CategoryProduct findByCategoryName(String name);
}
