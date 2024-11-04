package com.lotteon.repository.category;

import com.lotteon.entity.category.CategoryArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryArticleRepository extends JpaRepository<CategoryArticle, Long> {

    // 특정 카테고리 이름으로 조회
    Optional<CategoryArticle> findByCategoryName(String categoryName);

    // 부모 카테고리(1차 카테고리) 조회
    List<CategoryArticle> findByParentIsNull();

    // 특정 부모 카테고리(1차 카테고리)에 해당하는 자식 카테고리(2차 카테고리) 조회
    List<CategoryArticle> findByParent(CategoryArticle parent);

    // 특정 타입과 레벨에 해당하는 카테고리 조회
    List<CategoryArticle> findByCategoryTypeAndCategoryLevel(int categoryType, int categoryLevel);

    // 카테고리 이름, 레벨, 타입에 따른 카테고리 조회
    Optional<CategoryArticle> findByCategoryNameAndCategoryLevelAndCategoryType(String categoryName, int level, int type);

    // 특정 카테고리 ID로 자식 카테고리 조회
    @Query("SELECT c FROM CategoryArticle c WHERE c.parent.categoryId = :parentId")
    List<CategoryArticle> findByParentCategoryId(Long parentId);
}
