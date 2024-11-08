package com.lotteon.controller.controller;

import com.lotteon.dto.ArticleDto;
import com.lotteon.dto.responseDto.*;
import com.lotteon.entity.article.Notice;
import com.lotteon.entity.article.Qna;
import com.lotteon.service.VisitorService;
import com.lotteon.service.article.NoticeService;
import com.lotteon.service.article.QnaService;
import com.lotteon.service.config.*;
import com.lotteon.service.member.MemberService;
import com.lotteon.service.product.OrderItemService;
import com.lotteon.service.term.TermsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/config")
@RequiredArgsConstructor
@Log4j2
public class AdminConfigController {
    private final BannerService bannerService;
    private final CopyrightService copyrightService;
    private final VersionService versionService;
    private final TermsService termsService;
    private final VisitorService visitorService;
    private final NoticeService noticeService;
    private final OrderItemService orderItemService;
    private final MemberService memberService;
    private final QnaService qnaService;

    private String getSideValue() {
        return "config";  // 실제 config 값을 여기에 설정합니다.
    }

    @ModelAttribute
    public void pageIndex(Model model) {
        model.addAttribute("config",getSideValue());
    }

    @GetMapping("/index")
    public String index(Model model) {
        String key = "visitor:count:" + LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Long todayCount = visitorService.getVisitorCount(key);
        Long yesterdayCount = visitorService.findVisitorCount(yesterday);
        Long weekCount = visitorService.findVisitorCountOfWeek(LocalDate.now());
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("yesterdayCount", yesterdayCount);
        model.addAttribute("weekCount", weekCount);
        // 공지사항 리스트 모델에 추가
        List<Notice> noticeList = noticeService.getTop5Notices();
        model.addAttribute("notices", noticeList);
        // QnA 리스트 모델에 추가
        List<Qna> qnaList = qnaService.getTop5Qnas();
        model.addAttribute("qnas", qnaList);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59).withNano(0);

        Long beforeDeli = orderItemService.findItemCnt(0,startOfDay,endOfDay);
        Long afterDeli = orderItemService.findItemCnt(1,startOfDay,endOfDay);
        Long completeDeli = orderItemService.findItemCnt(4,startOfDay,endOfDay);
        Long cancleDeli = orderItemService.findItemCnt(6,startOfDay,endOfDay);
        Long exchangeDeli = orderItemService.findItemCnt(3,startOfDay,endOfDay);
        Long returnDeli = orderItemService.findItemCnt(2,startOfDay,endOfDay);
        Long successDeli = orderItemService.findItemCnt(5,startOfDay,endOfDay);

        Long orderCnt = orderItemService.findAllItemCnt(startOfDay,endOfDay);
        Long orderCntToday = orderItemService.findAllItemCnt(startOfDay.plusDays(7),endOfDay);
        Long orderCntYesterday = orderItemService.findAllItemCnt(startOfDay.plusDays(6),endOfDay.minusDays(1));

        int totalOrderPrice = orderItemService.findTotalPrice(startOfDay,endOfDay);
        int totalOrderPriceToday = orderItemService.findTotalPrice(startOfDay.plusDays(7),endOfDay);
        int totalOrderPriceYesterDay = orderItemService.findTotalPrice(startOfDay.plusDays(6),endOfDay.minusDays(1));

        Long signupCnt = memberService.findCnt(startOfDay,endOfDay);
        Long signupCntToday = memberService.findCnt(startOfDay.plusDays(7),endOfDay);
        Long signupCntYesterDay = memberService.findCnt(startOfDay.plusDays(6),endOfDay.minusDays(1));

        Long qnaCnt = qnaService.findCnt(startOfDay,endOfDay);
        Long qnaCntToday = qnaService.findCnt(startOfDay.plusDays(7),endOfDay);
        Long qnaCntYesterDay = qnaService.findCnt(startOfDay.plusDays(6),endOfDay.minusDays(1));

        model.addAttribute("beforeDeli", beforeDeli);
        model.addAttribute("afterDeli",afterDeli);
        model.addAttribute("completeDeli",completeDeli);
        model.addAttribute("cancleDeli",cancleDeli);
        model.addAttribute("exchangeDeli",exchangeDeli);
        model.addAttribute("returnDeli",returnDeli);
        model.addAttribute("successDeli",successDeli);

        model.addAttribute("orderCnt",orderCnt);
        model.addAttribute("orderCntToday",orderCntToday);
        model.addAttribute("orderCntYesterday",orderCntYesterday);

        model.addAttribute("totalPrice",totalOrderPrice);
        model.addAttribute("totalPriceToday",totalOrderPriceToday);
        model.addAttribute("totalPriceYesterDay",totalOrderPriceYesterDay);

        model.addAttribute("signupCnt",signupCnt);
        model.addAttribute("signupCntToday",signupCntToday);
        model.addAttribute("signupCntYesterDay",signupCntYesterDay);

        model.addAttribute("qnaCnt",qnaCnt);
        model.addAttribute("qnaCntToday",qnaCntToday);
        model.addAttribute("qnaCntYesterDay",qnaCntYesterDay);

        return "pages/admin/index";
    }
    @GetMapping("/basics")
    public String basic(Model model) {

        GetCopyrightDTO copyrightDTO = copyrightService.getRecentCopyright();
        model.addAttribute("copy", copyrightDTO);
        model.addAttribute("active","basics");
        return "pages/admin/config/basic";
    }
    @GetMapping("/banners")
    public String banner(Model model) {
        List<GetBannerDTO> bannerList = bannerService.findAllByCate(1);
        model.addAttribute("bannerList", bannerList);
        model.addAttribute("active","banners");
        return "pages/admin/config/banner";
    }
    @GetMapping("/terms")
    public String terms(Model model) {
        List<GetTermsResponseDto> terms = termsService.selectAllTerms();
        model.addAttribute("terms", terms);
        model.addAttribute("active","terms");
        return "pages/admin/config/term";
    }
    @GetMapping("/versions")
    public String version(Model model) {
        PageResponseDTO<GetVersionDTO> page = versionService.getPagedVersionList(1);
        model.addAttribute("resp", page);
        model.addAttribute("active","versions");
        return "pages/admin/config/version";
    }
    @GetMapping("/versions/{pg}")
    public String version(@PathVariable(value = "pg") Integer pg, Model model) {
        PageResponseDTO<GetVersionDTO> page = versionService.getPagedVersionList(pg);
        model.addAttribute("resp", page);
        model.addAttribute("active","versions");
        return "pages/admin/config/version";
    }


}
