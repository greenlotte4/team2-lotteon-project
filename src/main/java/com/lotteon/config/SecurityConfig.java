package com.lotteon.config;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ToString
@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasAnyRole("admin","seller")
                        .requestMatchers("/admin/config/**").hasRole("admin")

                        .requestMatchers("/admin/cs/**").hasRole("admin")
                        .requestMatchers("/my/**").authenticated()
                        .requestMatchers("/event/**").authenticated()
//                        .requestMatchers(HttpMethod.GET,"prod/order/**").authenticated()
//                        .requestMatchers(HttpMethod.GET,"prod/cart/**").authenticated()
                        .requestMatchers("/**","/error/**", "/file/**", "/auth/**","/cs/**", "/company/**", "/prod/**","/policy/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/auth/login/view")  // 로그인 페이지 URL
                        .loginProcessingUrl("/auth/login")  // 로그인 인증 처리 URL (디비 처리)
                        .defaultSuccessUrl("/")  // 로그인 성공 후 이동할 URL
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
                .httpBasic(Customizer.withDefaults())
                .build();
    }


//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails userDetails = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("1234"))
//                .roles("admin")
//                .build();
//
//        return new InMemoryUserDetailsManager(userDetails);
//    }

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
