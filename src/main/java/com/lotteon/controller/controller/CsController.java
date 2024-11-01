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
import jakarta.servlet.http.HttpServletRequest;
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
        // 최신 공지사항 10개를 가져와서 모델에 추가
        List<Notice> noticeList = noticeService.getTop10Notices();
        log.info("컨트롤러 노티스 "+noticeList.toString());
        model.addAttribute("notices", noticeList);

        // 최신 문의하기 리스트 5개
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
                Sort.by(Sort.Direction.DESC, "noticeRdate") // 공지사항 등록일 기준으로 정렬
        );

        Page<NoticeResponseDto> noticeList;
        if (category != null && !category.isEmpty()) {
            noticeList = noticeService.getNoticesByCate1(category, sortedPageable); // cate1으로 조회
        } else {
            noticeList = noticeService.getNotices(null, sortedPageable); // 전체 조회
        }

        model.addAttribute("notices", noticeList);
        model.addAttribute("selectedCate1", category); // 선택된 cate1 전달
        return "pages/cs/notice/list"; // list.html 파일로 이동
    }


    // 개별 공지사항 상세 페이지 매핑 추가
    @GetMapping("/notice")
    public String notice(Model model) {

        return "pages/cs/notice/view";
    }

    @GetMapping("/notice/{id}")
    public String notice(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.getNotice(id);  // NoticeResponseDto로 가져오기
        model.addAttribute("notice", notice);  // 모델에 추가
        return "pages/cs/notice/view";  // 뷰 파일로 이동
    }

    @GetMapping("/notice/view/{id}")
    public String getNoticeDetail(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.incrementViewsAndGetNotice(id);  // 조회수 증가 후 데이터 조회
        model.addAttribute("notice", notice);  // 조회된 데이터를 모델에 추가하여 뷰로 전달
        return "pages/cs/notice/view";  // 공지사항 상세보기 페이지로 이동
    }


    /* 자주묻는 질문*/
    /* TODO: 자주묻는질문 1,2차 유형별 목록 */
    // 자주 묻는 질문 페이지
    @GetMapping("/faqs")
    public String getFaqsByCategory(@RequestParam(required = false) String category, Model model) {
        // 기본 카테고리를 설정합니다. category가 없으면 "user"를 기본으로 사용합니다.
        final String selectedCategory = (category == null || category.isEmpty()) ? "user" : category;
        model.addAttribute("selectedCate1", selectedCategory);


        // 1차 카테고리를 조회합니다.
        CategoryArticle cate1 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(selectedCategory, 1, 2)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + selectedCategory));

        // 2차 카테고리 리스트를 가져옵니다.
        List<CategoryArticle> cate2List = categoryArticleRepository.findByParentCategoryId(cate1.getCategoryId());

        // 각 2차 카테고리별 FAQ를 Map 형태로 저장하고, 모델에 추가합니다.
        Map<String, List<ArticleDto>> faqMap = new HashMap<>();
        cate2List.forEach(cate2 -> {
            List<ArticleDto> faqList = faqService.getTop10FaqsByCategory(cate1, cate2); // FAQ 데이터 가져오기
            faqMap.put(cate2.getCategoryName(), faqList);  // cate2 이름을 키로 추가
        });

        model.addAttribute("faqMap", faqMap); // FAQ 데이터를 Map으로 전달
        return "pages/cs/faq/list";  // 뷰 파일로 이동
    }

    // FAQ 상세 보기
    @GetMapping("/faq/view/{id}")
    public String faqView(@PathVariable Long id, Model model) {
        // 서비스에서 FAQ 가져오기
        ArticleDto faq = faqService.getFaqById(id);
        model.addAttribute("faq", faq);

        // 1차 카테고리 정보 가져오기
        CategoryArticle cate1 = faq.getCate1(); // 1차 카테고리 엔티티
        if (cate1 != null) {
            model.addAttribute("selectedCate1", cate1.getCategoryName()); // 1차 카테고리 이름 설정
            model.addAttribute("categoryWarning", cate1.getCategoryWarning()); // 카테고리 경고 메시지 설정
        }

        return "pages/cs/faq/view"; // 상세보기 페이지로 이동
    }



    /* 문의하기 */
    // 카테고리별 QNA 목록 조회
    @GetMapping("/qnas")
    public String qnasByCategory(
            @RequestParam(required = false) String category,
            Model model,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // category가 null이거나 빈 문자열이면 "회원"을 기본값으로 설정 (전체 글 목록이 없어서)
        if (category == null || category.isEmpty()) {
            category = "user";
        }

        // 카테고리에 해당하는 QNA 목록 조회
        Page<ArticleDto> qnasPage = qnaService.getQnasByCategory(category, pageable);

        model.addAttribute("qnas", qnasPage.getContent());
        model.addAttribute("page", qnasPage);
        model.addAttribute("selectedCate1", category); // 선택된 카테고리 전달

        return "pages/cs/qna/list";
    }


    // QNA 글 보기
    @GetMapping("/qna/view/{id}")
    public String qnaView(@PathVariable Long id, Model model) {
        ArticleDto qna = qnaService.getQnaById(id); // 서비스에서 QNA 가져오기
        model.addAttribute("qna", qna); // 모델에 QNA 데이터 추가

        // answer가 존재할 경우에만 모델에 추가
        if (qna.getAnswer() != null && !qna.getAnswer().isEmpty()) {
            model.addAttribute("hasAnswer", true);
        } else {
            model.addAttribute("hasAnswer", false);
        }

        return "pages/cs/qna/view"; // 상세보기 페이지로 이동
    }


    @GetMapping("/qna/write")
    public String qnaWrite(Model model) {
        return "pages/cs/qna/write";
    }

    // 문의하기 글 작성 처리
    // @AuthenticationPrincipal 인증된 사용자 정보를 가져올 때 사용
    @PostMapping("/qna/write")
    public String submitQna(@ModelAttribute ArticleDto articleDto,  @AuthenticationPrincipal MyUserDetails myUserDetails) {
        if(myUserDetails == null)
        {
            return "redirect:/auth/login/view";
        }
        Long qnaId = qnaService.insertQna(articleDto, myUserDetails.getUser().getId());
        log.info("문의하기 글 작성 완료, ID: " + qnaId);
        return "redirect:/cs/qnas";
    }
}
