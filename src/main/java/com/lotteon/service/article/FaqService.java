package com.lotteon.service.article;

import com.lotteon.dto.ArticleDto;
import com.lotteon.entity.article.Faq;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.repository.article.FaqRepository;
import com.lotteon.repository.category.CategoryArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final ModelMapper modelMapper;
    // FAQ 목록 조회 (카테고리별)
    public Page<ArticleDto> list(CategoryArticle cate1Id, CategoryArticle cate2Id, int limit) {
        Pageable pageable = PageRequest.of(0, limit); // 0번째 페이지에서 limit 만큼 데이터 조회
        // Page<Faq>를 사용하여 페이징 처리된 결과를 받아옴
        Page<Faq> faqPage = faqRepository.findByCate1AndCate2(cate1Id, cate2Id, pageable);
        // Page<ArticleDto>로 변환하여 반환
        return faqPage.map(faq -> modelMapper.map(faq, ArticleDto.class));
    }

    // FAQ 상세 조회 (ID로 조회)
    public ArticleDto get(Long id) {
        Faq faq = faqRepository.findById(id).orElseThrow(() -> new RuntimeException("FAQ not found"));
        return modelMapper.map(faq, ArticleDto.class); // FAQ -> DTO 변환
    }

    // 더보기 기능
    public Page<ArticleDto> loadMore(CategoryArticle cate1Id, CategoryArticle cate2Id, int currentCount, int additionalCount) {
        Pageable pageable = PageRequest.of(0, currentCount + additionalCount); // 더보기로 추가 데이터 가져옴
        // Page<Faq>를 사용하여 더보기 페이징 처리된 결과를 받아옴
        Page<Faq> faqPage = faqRepository.findByCate1AndCate2(cate1Id, cate2Id, pageable);
        // Page<ArticleDto>로 변환하여 반환
        return faqPage.map(faq -> modelMapper.map(faq, ArticleDto.class));
    }


}
