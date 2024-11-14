package com.lotteon.config;

import com.lotteon.config.filter.CustomLoginFilter;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.service.MyOauth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ToString
@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer  {
    private final CustomLoginFilter customLoginFilter;
    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final CustomerRepository customerRepository;

    @Lazy
    @Autowired
    private MyOauth2UserService oauth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/config/**").hasRole("admin")
                        .requestMatchers("/admin/user/**").hasRole("admin")
                        .requestMatchers("/admin/shop/**").hasRole("admin")
                        .requestMatchers("/admin/prod/**").hasAnyRole("admin","seller")
                        .requestMatchers("/admin/coupon/**").hasAnyRole("admin","seller")
                        .requestMatchers("/admin/order/**").hasAnyRole("admin","seller")
                        .requestMatchers("/admin/cs/**").hasAnyRole("admin","seller")
                        .requestMatchers("/my/**").hasAnyRole("customer","guest")
                        .requestMatchers("/event/**").hasRole("customer")
                        .requestMatchers(HttpMethod.GET,"prod/order/**").authenticated()
                        .requestMatchers("/**","/error/**", "/file/**", "/auth/**","/cs/**", "/company/**", "/prod/**","/policy/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exection -> exection
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/"); // Redirect to main page on access denial
                        })
                )
                .formLogin(login -> login
                        .loginPage("/auth/login/view")  // 로그인 페이지 URL
                        .loginProcessingUrl("/auth/login")  // 로그인 인증 처리 URL (디비 처리)
                        .successHandler(customLoginFilter)
//                        .failureUrl("/auth/login?error=true")  // 로그인 실패 시 이동할 URL
                        .usernameParameter("userName")  // 사용자명 파라미터 이름
                        .passwordParameter("pwd")  // 비밀번호 파라미터 이름
                        .permitAll()  // 로그인 페이지는 인증 없이 접근 가능
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout","GET"))  // 절대 경로로 설정
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret") // 고유한 키를 설정합니다.
                        .tokenValiditySeconds(86400 * 7) // 7일 동안 유지
                        .tokenRepository(tokenRepository)
                        .rememberMeParameter("remember-me") // 클라이언트 측의 체크박스 파라미터 이름 (기본은 "remember-me")
                        .userDetailsService(userDetailsService) // 유저 정보를 제공할 UserDetailsService
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth/login/view") // 사용자 정의 로그인 페이지
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService)) // 사용자 정보 서비스 설정
                        .successHandler(customLoginFilter) // 로그인 성공 핸들러
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    protected CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

}
