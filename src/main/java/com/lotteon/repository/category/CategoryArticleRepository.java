package com.lotteon.repository.category;

import com.lotteon.entity.category.CategoryArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryArticleRepository extends JpaRepository<CategoryArticle, Long> {

    Optional<CategoryArticle> findByCategoryName(String categoryName);

    // 부모 카테고리(1차 카테고리) 조회
    List<CategoryArticle> findByParentIsNull();
    // 특정 부모 카테고리(1차 카테고리)에 해당하는 자식 카테고리(2차 카테고리) 조회
    List<CategoryArticle> findByParent(CategoryArticle parent);

    List<CategoryArticle> findAllByCategoryTypeAndCategoryLevel(int type, int level);

    Optional<CategoryArticle> findByCategoryNameAndCategoryLevelAndCategoryType(String categoryName1, int i, int i1);
}




