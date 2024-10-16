package com.lotteon.controller.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/cs")
@RequiredArgsConstructor
@Log4j2
public class AdminCsController {

    @GetMapping("/index")
    public String index(Model model) {
        return "pages/admin/cs/index";
    }
    @GetMapping("/faqs")
    public String faqs(Model model) {
        return "pages/admin/cs/faq/list";
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        return "pages/admin/cs/faq/view";
    }


    @GetMapping("/faq/write")
    public String faqWrite(Model model) {
        return "pages/admin/cs/faq/write";
    }

    @GetMapping("/faq/modify")
    public String faqModify(Model model) {
        return "pages/admin/cs/faq/modify";
    }

    @GetMapping("/notices")
    public String notices(Model model) {
        return "pages/admin/cs/notices";
    }

    @GetMapping("/notice")
    public String notice(Model model) {
        return "pages/admin/cs/notice/view";
    }

    @GetMapping("/notice/modify")
    public String noticeModify(Model model) {
        return "pages/admin/cs/notice/modify";
    }

    @GetMapping("/notice/write")
    public String noticeWrite(Model model) {
        return "pages/admin/cs/notice/write";
    }

    @GetMapping("/qnas")
    public String qnas(Model model) {
        return "pages/admin/cs/qna/list";
    }

    @GetMapping("/qna/modify")
    public String qnaModify() {
        return "pages/admin/cs/qna/modify";
    }

    @GetMapping("/qna")
    public String qnaView() {
        return "pages/admin/cs/qna/view";
    }

    @GetMapping("/qna/write")
    public String qnaWrite() {
        return "pages/admin/cs/qna/write";
    }

}
