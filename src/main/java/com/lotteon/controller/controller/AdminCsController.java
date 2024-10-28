package com.lotteon.controller.controller;

import com.lotteon.dto.ArticleDto;
import com.lotteon.dto.requestDto.PostRecruitDto;
import com.lotteon.service.article.FaqService;
import com.lotteon.service.article.QnaService;
import com.lotteon.service.article.RecruitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  이름 : 이상훈
 *  날짜 : 2024-10-25
 *  작업내용 : 채용목록 불러오기/ 페이징/ 검색
 *
 *
 * 수정이력
      - 2025/10/26 박경림 - /faqs pageable default로 sort 설정
      - 2025/10/26 박경림 - faq 개별 삭제&선택 삭제 기능, /qnas 문의하기 목록 추가


 * */

@Controller
@RequestMapping("/admin/cs")
@RequiredArgsConstructor
@Log4j2
public class AdminCsController {

    private final QnaService qnaService;
    private final FaqService faqService;
    private final RecruitService recruitService;


    @ModelAttribute
    public void pageIndex(Model model) {
        model.addAttribute("config", getSideValue());
    }

    private String getSideValue() {
        return "cs";  // 실제 config 값을 여기에 설정합니다.
    }


/*    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("config", getSideValue());
        return "pages/admin/cs/index";
    }*/


    /**
     * 관리자 faq 리스트 페이지 컨트롤러
     *
     * @param model    모델
     * @param pageable 페이지어블
     * @return 관리자 > cs > faq 리스트 페이지
     */

    // FAQ 자주묻는질문
    // 목록
    @GetMapping("/faqs")
    public String faqs(Model model, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // 1. faq 서비스에서 페이징 처리된  faq를 반환
        Page<ArticleDto> faqsPage = faqService.getAllFaqs(pageable);
        // 2. faqsPage에서 데이터들을 뽑아옴
        List<ArticleDto> faqs = faqsPage.getContent();
        System.out.println("faqs = " + faqs);
        // 3. model에 faqs 를 추가
        model.addAttribute("faqs", faqs);

        // 4. 웹 반환
        return "pages/admin/cs/faq/list";
    }



    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("active", "faqs");
        return "pages/admin/cs/faq/view";
    }


    @GetMapping("/faq/write")
    public String faqWrite(Model model) {
        return "pages/admin/cs/faq/write";
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



    // FAQ 상세 보기
    @GetMapping("/faq/view/{id}")
    public String faqView(@PathVariable Long id, Model model) {
        ArticleDto faq = faqService.getFaqById(id); // 서비스에서 FAQ 가져오기
        model.addAttribute("faq", faq); // 모델에 FAQ 데이터 추가
        return "pages/admin/cs/faq/view"; // 상세보기 페이지로 이동
    }

    // FAQ 수정하기
    @GetMapping("/faq/modify/{id}")
    public String faqModify(@PathVariable Long id, Model model) {
        ArticleDto faq = faqService.getFaqById(id);
        model.addAttribute("faq", faq);
        return "pages/admin/cs/faq/modify";
    }

    @PostMapping("/faq/modify/{id}")
    public  String modifyFaq(@PathVariable Long id, @ModelAttribute ArticleDto faqDto) {
        faqService.updateFaq(id, faqDto);
        return "redirect:/admin/cs/faqs";
    }

    // FAQ 삭제 처리
    @GetMapping("/faq/delete/{id}")
    public String deleteFaq1(@PathVariable Long id) {
        try {
            faqService.deleteFaq(id);
            return "redirect:/admin/cs/faqs"; // 성공 시 리다이렉트

        } catch (Exception e) {
            return "redirect:/admin/cs/faqs?error=true";
        }
    }


    @DeleteMapping("/faq/delete/{id}")
    public ResponseEntity<?> deleteFaq(@PathVariable Long id) {
        try {
            faqService.deleteFaq(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }



    // 선택 삭제
    @PostMapping("/faq/deleteSelected")
    @ResponseBody
    public Map<String, Object> deleteSelectedFaqs(@RequestBody List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            faqService.deleteSelectedFaqs(ids);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }











    // QNA 문의하기
    @GetMapping("/qnas")
    public String qnas(Model model, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // 1. qna 서비스에서 페이징 처리된  qna를 반환
        Page<ArticleDto>qnasPage = qnaService.getAllQnas(pageable);
        // 2. qnasPage에서 데이터들을 뽑아옴
        List<ArticleDto> qnas = qnasPage.getContent();
        // 3. model에 qnas 를 추가
        model.addAttribute("qnas", qnas); // 모델 이름을 qnas로 변경

        // 4. 웹 반환
        return "pages/admin/cs/qna/list";

    }

    // QNA 상세 보기 1028 코드 미완성
    @GetMapping("/qna/view/{id}")
    public String qnaView(@PathVariable Long id, Model model) {
        ArticleDto qna = qnaService.getQnaById(id); // 서비스에서 QNA 가져오기
        model.addAttribute("qna", qna); // 모델에 QNA 데이터 추가
        return "pages/admin/cs/qna/view"; // 상세보기 페이지로 이동
    }

    @GetMapping("/qna/modify")
    public String qnaModify(Model model) {
        model.addAttribute("config", getSideValue());
        return "pages/admin/cs/qna/modify";
    }

    @GetMapping("/qna")
    public String qnaView(Model model) {
        model.addAttribute("active", "qnas");
        return "pages/admin/cs/qna/view";
    }

    @GetMapping("/qna/write")
    public String qnaWrite(Model model) {
        model.addAttribute("active", "qnas");
        return "pages/admin/cs/qna/write";
    }

    @GetMapping("/recruits")
    public String recruits(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchType", defaultValue = "0") String searchType,
            @RequestParam(value = "keyword", defaultValue = "0") String keyword

    ) {
        model.addAttribute("active", "recruits");
        Page<PostRecruitDto> recruits;
        if (!searchType.equals("0") && !keyword.equals("0")) {
            recruits = recruitService.findAllBySearch(searchType, keyword, page);
        } else {
            recruits = recruitService.findAll(page);
        }
        model.addAttribute("recruits", recruits);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", recruits.getTotalPages());
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "pages/admin/cs/recruit/list";
    }



}
