package com.lotteon.controller.controller;

import com.lotteon.dto.ArticleDto;
import com.lotteon.service.article.FaqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.List;

@Controller
@RequestMapping("/admin/cs")
@RequiredArgsConstructor
@Log4j2
public class AdminCsController {
    @ModelAttribute
    public void pageIndex(Model model) {
        model.addAttribute("config",getSideValue());
    }
    private String getSideValue() {
        return "cs";  // 실제 config 값을 여기에 설정합니다.
    }
    private final FaqService faqService;


/*    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("config", getSideValue());
        return "pages/admin/cs/index";
    }*/
@GetMapping("/faqs")
public String faqs(Model model, Pageable pageable) {
    Page<ArticleDto> faqsPage = faqService.getAllFaqs(pageable);
    List<ArticleDto> faqs = faqsPage.getContent();
    System.out.println("faqs = " + faqs);
    model.addAttribute("faqs", faqs);
    model.addAttribute("active","faqs");

    return "pages/admin/cs/faq/list";
}

    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("active","faqs");
        return "pages/admin/cs/faq/view";
    }


    @GetMapping("/faq/write")
    public String faqWrite(Model model) {
        model.addAttribute("active","faqs");
        return "pages/admin/cs/faq/write";
    }


    // FAQ 상세 보기
    @GetMapping("/faq/view/{id}")
    public String faqView(@PathVariable Long id, Model model) {
        ArticleDto faq = faqService.getFaqById(id); // 서비스에서 FAQ 가져오기
        model.addAttribute("faq", faq); // 모델에 FAQ 데이터 추가
        model.addAttribute("active","faqs");
        return "pages/admin/cs/faq/view"; // 상세보기 페이지로 이동
    }



    @GetMapping("/faq/modify")
    public String faqModify(Model model) {
        model.addAttribute("active","faqs");
        return "pages/admin/cs/faq/modify";
    }

    @GetMapping("/qnas")
    public String qnas(Model model) {
        model.addAttribute("active","qnas");
        return "pages/admin/cs/qna/list";
    }

    @GetMapping("/qna/modify")
    public String qnaModify(Model model) {
        model.addAttribute("config", getSideValue());
        return "pages/admin/cs/qna/modify";
    }

    @GetMapping("/qna")
    public String qnaView(Model model) {
        model.addAttribute("active","qnas");
        return "pages/admin/cs/qna/view";
    }

    @GetMapping("/qna/write")
    public String qnaWrite(Model model) {
        model.addAttribute("active","qnas");
        return "pages/admin/cs/qna/write";
    }

    @GetMapping("/recruits")
    public String recruits(Model model) {
        model.addAttribute("active","recruits");
        return "pages/admin/cs/recruit/list";
    }
    // FAQ 작성
    @PostMapping("/faq/write")
    public String writeFaq(@RequestParam("faqCate") String category1,
                           @RequestParam("faqType") String category2,
                           @RequestParam("faqTitle") String title,
                           @RequestParam("faqContent") String content) {
        System.out.println("category1 = " + category1);
        System.out.println("category2 = " + category2);
        System.out.println("title = " + title);
        System.out.println("content = " + content);
        faqService.writeFaq(category1, category2, title, content);
        return "redirect:/admin/cs/faqs";
    }

}
