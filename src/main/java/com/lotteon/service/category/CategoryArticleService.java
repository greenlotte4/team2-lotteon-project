package com.lotteon.service.category;

import com.lotteon.dto.responseDto.GetArticleCategoryDto;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.repository.category.CategoryArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryArticleService {
    private final CategoryArticleRepository categoryArticleRepository;

    // 1차 카테고리(부모 카테고리) 가져오기
    public List<CategoryArticle> getParentCategories() {
        return categoryArticleRepository.findByParentIsNull();
    }

    // 2차 카테고리(자식 카테고리) 가져오기
    public List<CategoryArticle> getChildCategories(Long parentCategoryId) {
        CategoryArticle parentCategory = categoryArticleRepository.findById(parentCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부모 카테고리를 찾을 수 없습니다: " + parentCategoryId));
        return categoryArticleRepository.findByParent(parentCategory);
    }

    // 카테고리 ID로 카테고리 가져오기
    public CategoryArticle getCategoryById(Long cate1Id) {
        return categoryArticleRepository.findById(cate1Id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + cate1Id));
    }

    // 주어진 타입과 레벨에 따라 카테고리를 가져오는 메서드
    public List<GetArticleCategoryDto> findCategory(int categoryType, int categoryLevel) {
        List<CategoryArticle> articles = categoryArticleRepository.findByCategoryTypeAndCategoryLevel(categoryType, categoryLevel);

        log.info("조회된 카테고리 목록: " + articles);  // 조회 결과를 로그로 출력

        // 카테고리 목록을 DTO로 변환하여 반환
        return articles.stream()
                .map(CategoryArticle::toGetArticleCategoryDto)
                .collect(Collectors.toList());
    }

    // 카테고리 이름을 통해 카테고리를 가져오는 메서드 추가
    public CategoryArticle getCategoryByName(String categoryName) {
        return categoryArticleRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryName));
    }
}
