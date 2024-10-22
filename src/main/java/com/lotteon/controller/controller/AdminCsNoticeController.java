package com.lotteon.controller.controller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.NoticeRequestDto;
import com.lotteon.dto.responseDto.GetArticleCategoryDto;
import com.lotteon.dto.responseDto.NoticeResponseDto;
import com.lotteon.entity.article.Notice;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.service.article.NoticeService;
import com.lotteon.service.category.CategoryArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequestMapping("/admin/cs")
@RequiredArgsConstructor
public class AdminCsNoticeController {

    private final NoticeService noticeService;
    private final CategoryArticleService categoryArticleService;

    // 공지사항 목록 조회 (페이징 및 검색 추가)
    @GetMapping("/notices")
    public String notices(Model model) {
        // 서비스에서 공지사항 목록을 가져옴
        List<Notice> notice = noticeService.getAllNotices();
        // 모델에 공지사항 리스트를 추가
        model.addAttribute("notices", notice);
        return "pages/admin/cs/notice/list";  // 공지사항 목록 뷰로 이동
    }

    // 공지사항 상세 조회 (조회수 증가 로직 추가)
    @GetMapping("/notice/{id}")
    public String notice(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.incrementViewsAndGetNotice(id);  // 조회수 증가 후 공지사항 조회
        model.addAttribute("notice", notice);
        return "pages/admin/cs/notice/view";
    }

    // 공지사항 작성 폼
    @GetMapping("/notice/write")
    public String noticeWriteForm(Model model, Authentication auth) {
        MyUserDetails user = (MyUserDetails) auth.getPrincipal();
        model.addAttribute("user", user);

        // 1차 카테고리 가져오기
        List<GetArticleCategoryDto> cate1 = categoryArticleService.findCategory(1, 1);
        model.addAttribute("cate1", cate1);

        // 2차 카테고리 가져오기
        List<GetArticleCategoryDto> cate2 = categoryArticleService.findCategory(1, 2);
        model.addAttribute("cate2", cate2);

        return "pages/admin/cs/notice/write";
    }

    // 2차 카테고리 동적 로드 API
    @GetMapping("/notice/categories/child")
    @ResponseBody
    public List<GetArticleCategoryDto> getChildCategories(@RequestParam Long parentId) {
        List<GetArticleCategoryDto> childCategories = categoryArticleService.getChildCategories(parentId)
                .stream()
                .map(CategoryArticle::toGetArticleCategoryDto)
                .collect(Collectors.toList());
        return childCategories;
    }

    // 공지사항 작성 처리
    @PostMapping("/notice/write")
    public String noticeWrite(@ModelAttribute NoticeRequestDto Dto) {
        log.info("컨트롤러 " + Dto);
        noticeService.saveNotice(Dto);
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
