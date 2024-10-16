package com.lotteon.controller.apicontroller;

import com.lotteon.entity.config.Banner;
import com.lotteon.service.config.BannerService;
import com.lotteon.service.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
public class ApiConfigController {
    private final ConfigService configService;
    private final BannerService bannerService;

    public ResponseEntity<?> insertBanner(Banner banner) {
        return null;
    }
}
