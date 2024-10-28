package com.lotteon.controller.controller;

import com.lotteon.dto.ArticleDto;
import com.lotteon.entity.article.Notice;
import com.lotteon.repository.category.CategoryArticleRepository;
import com.lotteon.service.article.FaqService;
import com.lotteon.service.article.NoticeService;
import com.lotteon.service.article.QnaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return "pages/cs/index";
    }

    @GetMapping("/notices")
    public String notices(Model model) {
        return "pages/cs/notice/list";  // 공지사항 페이지에 맞게 수정
    }

    // 개별 공지사항 상세 페이지 매핑 추가
    @GetMapping("/notice/view/{id}")
    public String notice(@PathVariable Long id, Model model) {
        // NoticeService를 통해 공지사항 조회
        Notice notice = noticeService.getNoticeById(id);
        model.addAttribute("notice", notice);  // 모델에 공지사항 데이터 추가
        return "pages/cs/notice/view";  // view.html 페이지로 이동
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
    @PostMapping("/qna/write")
    public String submitQna(@ModelAttribute ArticleDto articleDto, HttpServletRequest req) {
        Long qnaId = qnaService.insertQna(articleDto, req);
        log.info("문의하기 글 작성 완료, ID: " + qnaId);
        return "redirect:/cs/qnas";
    }
}
