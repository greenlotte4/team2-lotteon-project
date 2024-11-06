package com.lotteon.controller.apicontroller;

import com.lotteon.dto.responseDto.GetAdminUserDTO;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.service.AuthService;
import com.lotteon.service.member.CustomerService;
import groovy.util.logging.Log4j2;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
@Log4j2
public class ApiMyUserController {
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    // 나의설정 정보조회
//    @GetMapping("/info/{id}")
//    public ResponseEntity<?> myInfo(@PathVariable("id") Long id, Model model) {
//        GetAdminUserDTO getCust = customerService.findById(id);
//        model.addAttribute("cust", getCust);
//        //Optional<Customer> custOpt = customerRepository.findById(id);
//
//        Object myInfo;
//        return ResponseEntity.ok().build(myInfo);
//    }



}
