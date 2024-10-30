package com.lotteon.controller.controller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.ArticleDto;
import com.lotteon.dto.responseDto.NoticeResponseDto;
import com.lotteon.entity.article.Notice;
import com.lotteon.entity.article.Qna;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*
 *  이름 : 박경림
 *  날짜 : 2024-10-30
 *  작업내용 : index에서 qna 출력
 *
 *
 * 수정이력
      -

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
        model.addAttribute("notices", noticeList);

        // 최신 문의하기 리스트 5개
        List<Qna> qnaList = qnaService.getTop5Qnas();
        model.addAttribute("qnas", qnaList);
        return "pages/cs/index";
    }

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

    @GetMapping("/faqs")
    public String faqs(Model model) {
        return "pages/cs/faq/list";
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        return "pages/cs/faq/view";
    }

    @GetMapping("/qnas")
    public String qnas(Model model) {
        return "pages/cs/qna/list";
    }

    @GetMapping("/qna")
    public String qna(Model model) {
        return "pages/cs/qna/view";
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
