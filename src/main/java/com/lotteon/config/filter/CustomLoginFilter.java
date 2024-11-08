package com.lotteon.config.filter;

import com.lotteon.config.MyUserDetails;
import com.lotteon.entity.member.Customer;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.service.AuthService;
import com.lotteon.service.member.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CustomLoginFilter implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final SavedRequestAwareAuthenticationSuccessHandler defaultSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private MemberService memberService;

    @Autowired
    public CustomLoginFilter(@Lazy MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
        Customer customer = user.getUser().getCustomer();
        if(user.getUser().getMemRole().equals("admin")) {
            redirectStrategy.sendRedirect(request, response, "/admin/config/index");
            return;
        }
        if(customer == null) {
            defaultSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            return;
        }
        if(!user.getUser().getMemState().equals("sleep")){
            memberService.updateLastLoginDate(user);
        }

        if(user.getUser().getMemState().equals("leave")){
            request.getSession().invalidate();
            redirectStrategy.sendRedirect(request, response, "/?memState=leave");
        }

        if(user.getUser().getMemState().equals("stop")){
            request.getSession().invalidate();
            redirectStrategy.sendRedirect(request, response, "/?memState=stop");
        }

        LocalDate today = LocalDate.now();
        String birth = customer.getCustBirth();

        LocalDate birthDate = LocalDate.parse(birth);

        if (today.getMonth() == birthDate.getMonth() && today.getDayOfMonth() == birthDate.getDayOfMonth()) {
            redirectStrategy.sendRedirect(request, response, "/event/birth");
        } else {
            defaultSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
