package com.lotteon.controller.controller;

import com.lotteon.dto.requestDto.GetDeliveryDto;
import com.lotteon.dto.responseDto.GetAdminUserDTO;
import com.lotteon.dto.responseDto.GetPointsDto;
import com.lotteon.service.AuthService;
import com.lotteon.service.point.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/admin/user")
@Controller
@RequiredArgsConstructor
@Log4j2
public class AdminUserController {

    private final AuthService authService;
    private final PointService pointService;

    @ModelAttribute
    public void pageIndex(Model model) {
        model.addAttribute("config",getSideValue());
    }
    private String getSideValue() {
        return "user";  // 실제 config 값을 여기에 설정합니다.
    }

    // 1. 관리자 회원목록
    @GetMapping("/user")
    public String user(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "searchType", defaultValue = "0") String searchType,
            @RequestParam(name = "keyword", defaultValue = "0") String keyword

    ) {
        // active라는 속성에 "users" 값을 추가해서 뷰에 전달 (현재 활성화된 페이지나 섹션을 표시)
        model.addAttribute("active","users");
        Page<GetAdminUserDTO> cust2;

        if(searchType.equals("0")){
            cust2 = authService.selectCustAndGuestAll(page);
        } else {
            cust2 = authService.findAllSearchType(page,searchType,keyword);
        }

        // 페이지 처리
        //Page<GetAdminUserDTO> cust2 = authService.selectCustAll2(page);

        // 2-1. 회원목록 모델에 담아서 뷰에서 보기
        model.addAttribute("customers", cust2);
        model.addAttribute("active","user");

        // 3-1. 페이지네이션
        model.addAttribute("page",page);
        // model.addAttribute("totalPages",customers.getTotalPages());
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages",cust2.getTotalPages());

        // 6. 관리자 회원목록 으로 이동
        return "pages/admin/user/user";
    }

    // 2. 관리자 포인트 관리
    @GetMapping("/point")
    public String point(
            Model model,
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "searchType",defaultValue = "0") String searchType,
            @RequestParam(name = "keyword",defaultValue = "0") String keyword
    ) {
        model.addAttribute("active","point");
        Page<GetPointsDto> points;

        if(!searchType.equals("0")&&!keyword.equals("0")) {
            points = pointService.findAllByAdminSearch(page,searchType,keyword);
        } else {
            points = pointService.findAll(page);
        }
        model.addAttribute("points", points);
        model.addAttribute("totalPages",points.getTotalPages());
        model.addAttribute("page",page);
        model.addAttribute("searchType",searchType);
        model.addAttribute("keyword",keyword);

        return "pages/admin/user/point";
    }
}
