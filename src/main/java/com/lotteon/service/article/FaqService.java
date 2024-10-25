package com.lotteon.service.article;

import com.lotteon.dto.ArticleDto;
import com.lotteon.entity.article.Faq;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.repository.article.FaqRepository;
import com.lotteon.repository.category.CategoryArticleRepository;
import com.lotteon.service.category.CategoryArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;
    private final CategoryArticleRepository categoryArticleRepository;
    private final CategoryArticleService categoryArticleService;
    private final ModelMapper modelMapper;

    /* 관리자 cs 기능 */
    public void writeFaq(String categoryName1, String categoryName2, String title, String content) {
        System.out.println("categoryName1 = " + categoryName1);

        // 1. CategoryArticleRepository에서 카테고리 이름으로 카테고리를 찾음
        CategoryArticle category1 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(categoryName1,1,2)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 1을 찾을 수 없습니다: " + categoryName1));
        CategoryArticle category2 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(categoryName2,2,2)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 2를 찾을 수 없습니다: " + categoryName2));

        System.out.println("category1 = " + category1.getCategoryId());
        System.out.println("category2 = " + category2.getCategoryId());
        // 2. Faq 엔티티 생성 및 저장
        Faq faq = Faq.builder()
                .cate1(category1)
                .cate2(category2)
                .faqTitle(title)
                .faqContent(content)
                .build();

        // 3. FAQ 저장
        faqRepository.save(faq);  // DB에 저장

    }

    // FAQ 목록 조회 (카테고리별)
    public Page<ArticleDto> getFaqs(CategoryArticle cate1, CategoryArticle cate2, int limit, Pageable pageable) {
        // Page<Faq>를 사용하여 페이징 처리된 결과를 받아옴
        Page<Faq> faqPage = faqRepository.findByCate1AndCate2(cate1, cate2, pageable);
        // Page<ArticleDto>로 변환하여 반환
        return faqPage.map(faq -> modelMapper.map(faq, ArticleDto.class));
    }



    // FAQ 상세보기 기능 추가
    public ArticleDto getFaqById(Long id) {
        // ID를 통해 FAQ 조회
        return faqRepository.findById(id)
                .map(ArticleDto::fromEntity) // DTO로 변환
                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다."));
    }


    /* 일반 cs 기능 */
    // 더보기 기능 (처음부터 10개 가져오는걸로 수정하기)
    public List<ArticleDto> getFaqsCount10(CategoryArticle cate1, CategoryArticle cate2) {
        Limit limit = Limit.of(10);

        // Page<Faq>를 사용하여 더보기 페이징 처리된 결과를 받아옴
        List<Faq> byCate1AndCate2 = faqRepository.findByCate1AndCate2(cate1, cate2, limit);
        // Page<ArticleDto>로 변환하여 반환
        return byCate1AndCate2.stream().map(faq -> modelMapper.map(faq, ArticleDto.class)).toList();

    }

    public Page<ArticleDto> getAllFaqs(Pageable pageable) {
        Page<Faq> faqPage = faqRepository.findAll(pageable);
        System.out.println("faqPage.getContent() = " + faqPage.getContent());

        return faqPage.map(ArticleDto::fromEntity);

    }

    // FAQ 상세 조회 (ID로 조회)
    public ArticleDto getDetailById(Long id) {
        Faq faq = faqRepository.findById(id).orElseThrow(() -> new RuntimeException("FAQ not found"));
        return modelMapper.map(faq, ArticleDto.class); // FAQ -> DTO 변환
    }


}
