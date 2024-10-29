package com.lotteon.controller.controller;

import com.lotteon.dto.responseDto.GetTermsResponseDto;
import com.lotteon.service.term.TermsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/policy")
@RequiredArgsConstructor
@Log4j2
public class PolicyController {

    private final TermsService termsService;

    @GetMapping("/{type}")
    public String customer(@PathVariable("type") String type, Model model) {
        log.info(type);
        List<GetTermsResponseDto> list = termsService.selectAllTerms();
        int i = 0;
        switch (type) {
            case "seller" -> i=1;
            case "finance" -> i=2;
            case "location" -> i=3;
            case "privacy" -> i=4;
            default -> i=0;
        }
        model.addAttribute("type", i);
        model.addAttribute("terms", list.get(i));
        return "pages/policy/terms";
    }

}
