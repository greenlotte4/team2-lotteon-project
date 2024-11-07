package com.lotteon.service.article;

import com.lotteon.dto.ArticleDto;
import com.lotteon.entity.article.Faq;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.repository.article.FaqRepository;
import com.lotteon.repository.category.CategoryArticleRepository;
import com.lotteon.service.category.CategoryArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    // 1차 및 2차 카테고리 기반 FAQ 조회 메서드
    public Page<ArticleDto> getFaqsByCate1AndCate2(String cate1Name, String cate2Name, Pageable pageable) {
        // 카테고리 이름으로 카테고리 객체 조회
        CategoryArticle cate1 = categoryArticleService.getCategoryByName(cate1Name);
        CategoryArticle cate2 = categoryArticleService.getCategoryByName(cate2Name);

        // FAQ 목록을 페이징 처리하여 조회
        Page<Faq> faqPage = faqRepository.findByCate1AndCate2(cate1, cate2, pageable);
        return faqPage.map(faq -> modelMapper.map(faq, ArticleDto.class)); // DTO로 변환하여 반환
    }


    public long getTotalCount() {
        return faqRepository.count();  // JpaRepository의 count 메서드를 통해 전체 개수를 반환
    }



    // FAQ 상세보기 기능 추가
    public ArticleDto getFaqById(Long id) {
        // ID를 통해 FAQ 조회 & 조회수 증가
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다. ID: " + id));

        faq.addView(); // 조회수 증가
        faqRepository.save(faq); // 변경된 조회수 저장

        return ArticleDto.fromEntity(faq); // DTO로 변환하여 반환
    }


    // FAQ 수정
    public ArticleDto updateFaq (Long id, ArticleDto articleDto) {

        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 자주묻는질문을 찾을 수 없습니다. ID: " + id));

        CategoryArticle cate1 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(articleDto.getCate1Name(),1,2)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 1을 찾을 수 없습니다."));
        CategoryArticle cate2 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(articleDto.getCate2Name(),2,2)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 2을 찾을 수 없습니다."));

        faq.update(articleDto.getTitle(), articleDto.getContent(), cate1, cate2);
        faqRepository.save(faq);
        return ArticleDto.fromEntity(faq);
    }


    // FAQ 삭제
    public void deleteFaq(Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 자주묻는질문을 찾을 수 없습니다. ID: " + id));
        faqRepository.delete(faq);
    }

    // 선택 삭제
    public void deleteSelectedFaqs(List<Long> ids) {
        List<Faq> faqsToDelete = faqRepository.findAllById(ids);

        if (faqsToDelete.isEmpty()) {
            throw new IllegalArgumentException("삭제할 자주묻는질문이 없습니다.");
        }

        faqRepository.deleteAllById(ids);
    }







    /* 일반 cs 기능 */
    // 카테고리별 FAQ 목록 조회

    // 더보기 기능으로 최대 10개의 FAQ를 가져옴    10개까지 FAQ 데이터를 불러오고, 제목을 ArticleDto로 매핑하여 반환
    public List<ArticleDto> getTop10FaqsByCategory(CategoryArticle cate1, CategoryArticle cate2) {
        List<Faq> faqs = faqRepository.findTop10ByCate1AndCate2OrderByFaqRdateDesc(cate1, cate2);
        return faqs.stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }





    /**
     * faq를 페이지네이션으로 들고오는 함수
     *
     * @param pageable
     * @return articleDTO 페이징
     */

    public Page<ArticleDto> getAllFaqs(Pageable pageable) {
        Page<Faq> faqPage = faqRepository.findAll(pageable);
        return faqPage.map(ArticleDto::fromEntity); // FAQ 엔티티를 ArticleDto로 변환
    }

    // FAQ 상세 조회 (ID로 조회)
    public ArticleDto getDetailById(Long id) {
        Faq faq = faqRepository.findById(id).orElseThrow(() -> new RuntimeException("FAQ not found"));
        return modelMapper.map(faq, ArticleDto.class); // FAQ -> DTO 변환
    }

    public CategoryArticle getCategoryByName(String categoryName) {
        return categoryArticleRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryName));
    }


    public Page<ArticleDto> getNotAllFaqs(String cate1, String cate2, Pageable pageable) {
        CategoryArticle categoryArticle = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(cate1,1,2).get();
        CategoryArticle categoryArticle2 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(cate2,2,2).get();
        Page<Faq> faqs = faqRepository.findAllByCate1AndCate2(categoryArticle,categoryArticle2,pageable);
        return faqs.map(ArticleDto::fromEntity);

    }
}
