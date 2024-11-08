package com.lotteon.service;

import com.lotteon.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WebSocketService {

    private final ProductService productService;

    @Scheduled(fixedRate = 90000) // 600,000ms = 10분
    public void refreshBestProducts() {
        productService.updateBestItems();
    }
}
