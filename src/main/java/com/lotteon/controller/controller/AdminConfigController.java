package com.lotteon.controller.controller;

import com.lotteon.dto.responseDto.*;
import com.lotteon.service.VisitorService;
import com.lotteon.service.config.*;
import com.lotteon.service.term.TermsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

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
        String yesterday = "visitor:count:" + LocalDate.now().minusDays(1);
        Long todayCount = visitorService.getVisitorCount(key);
        Long yesterdayCount = visitorService.getVisitorCount(yesterday);
        Long weekCount = visitorService.getWeekVisitorCount();
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("yesterdayCount", yesterdayCount);
        model.addAttribute("weekCount", weekCount);

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
        return "pages/admin/config/version";
    }


}
