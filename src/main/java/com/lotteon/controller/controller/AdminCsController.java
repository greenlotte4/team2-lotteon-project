package com.lotteon.controller.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/cs")
@RequiredArgsConstructor
public class AdminCsController {

    @GetMapping(value = {"/faq","/faq/","/faq/list"})
    public String faqList() {
        return "pages/admin/cs/faq/list";
    }

    @GetMapping("/faq/modify")
    public String faqModify() {
        return "pages/admin/cs/faq/modify";
    }

    @GetMapping("/faq/view")
    public String faqView() {
        return "pages/admin/cs/faq/view";
    }

    @GetMapping("/faq/write")
    public String faqWrite() {
        return "pages/admin/cs/faq/write";
    }


    @GetMapping(value = {"/notice","/notice/","/notice/list"})
    public String noticeList() {
        return "pages/admin/cs/notice/list";
    }

    @GetMapping("/notice/modify")
    public String noticeModify() {
        return "pages/admin/cs/notice/modify";
    }

    @GetMapping("/notice/view")
    public String noticeView() {
        return "pages/admin/cs/notice/view";
    }

    @GetMapping("/notice/write")
    public String noticeWrite() {
        return "pages/admin/cs/notice/write";
    }


    @GetMapping(value = {"/qna","/qna/","/qna/list"})
    public String qnaList() {
        return "pages/admin/cs/qna/list";
    }

    @GetMapping("/qna/modify")
    public String qnaModify() {
        return "pages/admin/cs/qna/modify";
    }

    @GetMapping("/qna/view")
    public String qnaView() {
        return "pages/admin/cs/qna/view";
    }

    @GetMapping("/qna/write")
    public String qnaWrite() {
        return "pages/admin/cs/qna/write";
    }

}
