package com.lotteon.controller.controller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.ArticleDto;
import com.lotteon.dto.responseDto.NoticeResponseDto;
import com.lotteon.entity.article.Notice;
import com.lotteon.entity.article.Qna;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.repository.category.CategoryArticleRepository;
import com.lotteon.service.article.FaqService;
import com.lotteon.service.article.NoticeService;
import com.lotteon.service.article.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  이름 : 박경림
 *  날짜 : 2024-10-30
 *  작업내용 : index에서 글 목록, 문의하기 글 목록, 글 보기
 *
 *
 * 수정이력
      - 2024/10/31 박경림 - CS qna 카테고리별 QNA 목록 조회 컨트롤러 추가
      - 2024/11/04 신승우 - 카테고리 필터링 기능 추가
 * */

@Controller
@RequestMapping("/cs")
@RequiredArgsConstructor
@Log4j2
public class CsController {

    private final FaqService faqService;
    private final QnaService qnaService;
    private final CategoryArticleRepository categoryArticleRepository;
    private final NoticeService noticeService;

    @GetMapping("/index")
    public String join(Model model) {
        List<Notice> noticeList = noticeService.getTop10Notices();
        log.info("컨트롤러 노티스 " + noticeList);
        model.addAttribute("notices", noticeList);

        List<Qna> qnaList = qnaService.getTop5Qnas();
        model.addAttribute("qnas", qnaList);
        return "pages/cs/index";
    }

    /* 공지사항 */
    @GetMapping("/notices")
    public String notices(@RequestParam(required = false) String category, Model model, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "noticeRdate")
        );

        Page<NoticeResponseDto> noticeList;
        if (category != null && !category.isEmpty()) {
            noticeList = noticeService.getNoticesByCate1(category, sortedPageable);
            CategoryArticle cate1 = categoryArticleRepository.findByCategoryName(category)
                    .orElse(null);
            model.addAttribute("categoryWarning", cate1 != null ? cate1.getCategoryWarning() : null);
        } else {
            noticeList = noticeService.getNotices(null, sortedPageable);
        }

        model.addAttribute("notices", noticeList);
        model.addAttribute("selectedCate1", category);
        return "pages/cs/notice/list";
    }

    @GetMapping("/notice/view/{id}")
    public String getNoticeDetail(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.incrementViewsAndGetNotice(id);
        model.addAttribute("notice", notice);

        // 공지사항 상세 보기에서 카테고리 경고 메시지 전달
        CategoryArticle cate1 = categoryArticleRepository.findByCategoryName(notice.getCategory1Name())
                .orElse(null);
        model.addAttribute("categoryWarning", cate1 != null ? cate1.getCategoryWarning() : null);

        return "pages/cs/notice/view";
    }

    /* 자주 묻는 질문 */
    @GetMapping("/faqs")
    public String getFaqsByCategory(@RequestParam(required = false) String category, Model model) {
        final String selectedCategory = (category == null || category.isEmpty()) ? "user" : category;
        model.addAttribute("selectedCate1", selectedCategory);

        CategoryArticle cate1 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(selectedCategory, 1, 2)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + selectedCategory));

        List<CategoryArticle> cate2List = categoryArticleRepository.findByParentCategoryId(cate1.getCategoryId());

        Map<String, List<ArticleDto>> faqMap = new HashMap<>();
        cate2List.forEach(cate2 -> {
            List<ArticleDto> faqList = faqService.getTop10FaqsByCategory(cate1, cate2);
            faqMap.put(cate2.getCategoryName(), faqList);
        });

        // FAQ 목록에서 카테고리 경고 메시지 전달
        model.addAttribute("categoryWarning", cate1.getCategoryWarning());
        model.addAttribute("faqMap", faqMap);
        return "pages/cs/faq/list";
    }

    @GetMapping("/faq/view/{id}")
    public String faqView(@PathVariable Long id, Model model) {
        ArticleDto faq = faqService.getFaqById(id);
        model.addAttribute("faq", faq);

        CategoryArticle cate1 = faq.getCate1();
        if (cate1 != null) {
            model.addAttribute("selectedCate1", cate1.getCategoryName());
            model.addAttribute("categoryWarning", cate1.getCategoryWarning());
        }

        return "pages/cs/faq/view";
    }

    /* 문의하기 */
    @GetMapping("/qnas")
    public String qnasByCategory(
            @RequestParam(required = false) String category,
            Model model,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        if (category == null || category.isEmpty()) {
            category = "user";
        }

        Page<ArticleDto> qnasPage = qnaService.getQnasByCategory(category, pageable);

        model.addAttribute("qnas", qnasPage.getContent());
        model.addAttribute("page", qnasPage);
        model.addAttribute("selectedCate1", category);

        return "pages/cs/qna/list";
    }

/*    @GetMapping("/qna/view/{id}")
    public String qnaView(@PathVariable Long id, @RequestParam(value = "selectedCate1", required = false) String selectedCate1,
                          Model model) {
        ArticleDto qna = qnaService.getQnaById(id);
        model.addAttribute("qna", qna);
        model.addAttribute("hasAnswer", qna.getAnswer() != null && !qna.getAnswer().isEmpty());
        return "pages/cs/qna/view";
    }*/

    @GetMapping("/qna/view/{id}")
    public String qnaView(@PathVariable Long id, Model model) {
        ArticleDto qna = qnaService.getQnaById(id);
        model.addAttribute("qna", qna);
        model.addAttribute("hasAnswer", qna.getAnswer() != null && !qna.getAnswer().isEmpty());

        // QnA의 1차 카테고리 정보 가져와서 모델에 추가
        CategoryArticle cate1 = qna.getCate1();
        if (cate1 != null) {
            model.addAttribute("selectedCate1", cate1.getCategoryName()); // 카테고리 이름 설정
            model.addAttribute("categoryWarning", cate1.getCategoryWarning()); // 카테고리 경고 메시지 설정 (필요 시)
        }

        return "pages/cs/qna/view";
    }


    @GetMapping("/qna/write")
    public String qnaWrite() {
        return "pages/cs/qna/write";
    }

    @PostMapping("/qna/write")
    public String submitQna(@ModelAttribute ArticleDto articleDto, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        if (myUserDetails == null) {
            return "redirect:/auth/login/view";
        }
        Long qnaId = qnaService.insertQna(articleDto, myUserDetails.getUser().getId());
        log.info("문의하기 글 작성 완료, ID: " + qnaId);
        return "redirect:/cs/qnas";
    }
}
