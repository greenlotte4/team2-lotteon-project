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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequestMapping("/admin/cs")
@RequiredArgsConstructor
public class AdminCsNoticeController {

    private final NoticeService noticeService;
    private final CategoryArticleService categoryArticleService;

    // 공지사항 목록 조회 (시간순 정렬 추가)
    @GetMapping("/notices")
    public String notices(Model model) {
        // 시간순 정렬된 공지사항 목록을 가져옴
        List<Notice> notices = noticeService.getAllNoticesSortedByDate();
        model.addAttribute("notices", notices);
        return "pages/admin/cs/notice/list";  // 공지사항 목록 뷰로 이동
    }

    // 공지사항 상세 조회 (조회수 증가 포함)
    @GetMapping("/notice/{id}")
    public String notice(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.incrementViewsAndGetNotice(id);
        model.addAttribute("notice", notice);  // 공지사항 데이터를 모델에 추가
        return "pages/admin/cs/notice/view";  // 상세 페이지 뷰 반환
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
        return "redirect:/admin/cs/notices";
    }

    // 공지사항 수정 폼
    @GetMapping("/notice/modify/{id}")
    public String noticeModifyForm(@PathVariable Long id, Model model) {
        NoticeResponseDto notice = noticeService.getNotice(id);
        model.addAttribute("notice", notice);
        return "pages/admin/cs/notice/modify";  // 수정 폼으로 이동
    }

    // 공지사항 수정 처리
    @PostMapping("/notice/modify/{id}")
    public String noticeModify(@PathVariable Long id, @ModelAttribute NoticeRequestDto noticeRequestDto) {
        noticeService.updateNotice(id, noticeRequestDto);  // 수정 처리
        return "redirect:/admin/cs/notice/" + id;
    }

    // 공지사항 삭제 처리 (DELETE 요청)
    @DeleteMapping("/notice/delete/{id}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok().build();  // 성공 응답
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }

    // 선택된 공지사항 삭제
    @PostMapping("/notice/deleteSelected")
    @ResponseBody
    public Map<String, Object> deleteSelectedNotices(@RequestBody List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            noticeService.deleteSelectedNotices(ids);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

}
