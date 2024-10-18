package com.lotteon.controller.controller;

import com.lotteon.dto.requestDto.NoticeRequestDto;
import com.lotteon.dto.responseDto.NoticeResponseDto;
import com.lotteon.service.article.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/cs")
@RequiredArgsConstructor
public class AdminCsNoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 조회 (페이징 및 검색 추가)
    @GetMapping("/notices")
    public String notices(@RequestParam(required = false) String keyword, Pageable pageable, Model model) {
        Page<NoticeResponseDto> notices = noticeService.getNotices(keyword, pageable);
        model.addAttribute("notices", notices);
        model.addAttribute("keyword", keyword);
        return "pages/admin/cs/notices";
    }

    // 공지사항 상세 조회
    @GetMapping("/notice/{id}")
    public String notice(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.getNotice(id);
        model.addAttribute("notice", notice);
        return "pages/admin/cs/notice/view";
    }

    // 공지사항 작성 폼
    @GetMapping("/notice/write")
    public String noticeWriteForm(Model model) {
        model.addAttribute("notice", new NoticeRequestDto());  // 빈 폼을 전달
        return "pages/admin/cs/notice/write";
    }

    // 공지사항 작성 처리
    @PostMapping("/notice/write")
    public String noticeWrite(@ModelAttribute NoticeRequestDto noticeRequestDto) {
        noticeService.saveNotice(noticeRequestDto);
        return "redirect:/admin/cs/notices";  // 작성 후 목록 페이지로 이동
    }

    // 공지사항 수정 폼
    @GetMapping("/notice/modify/{id}")
    public String noticeModifyForm(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.getNotice(id);
        model.addAttribute("notice", notice);
        return "pages/admin/cs/notice/modify";
    }

    // 공지사항 수정 처리
    @PostMapping("/notice/modify/{id}")
    public String noticeModify(@PathVariable Long id, @ModelAttribute NoticeRequestDto noticeRequestDto) {
        noticeService.updateNotice(id, noticeRequestDto);
        return "redirect:/admin/cs/notice/" + id;
    }

    // 공지사항 삭제 처리
    @PostMapping("/notice/delete/{id}")
    public String noticeDelete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/admin/cs/notices";
    }
}
