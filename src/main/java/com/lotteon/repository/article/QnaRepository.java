package com.lotteon.repository.article;

import com.lotteon.entity.article.Qna;
import com.lotteon.entity.category.CategoryArticle;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaRepository extends JpaRepository<Qna,Long> {

    // 1차 및 2차 카테고리로 FAQ 목록을 조회 (페이징 처리 포함)
    Page<Qna> findByCate1AndCate2(CategoryArticle cate1Id, CategoryArticle cate2Id, Pageable pageable);
    List<Qna> findByCate1AndCate2(CategoryArticle cate1Id, CategoryArticle cate2Id, Limit limit);


}
