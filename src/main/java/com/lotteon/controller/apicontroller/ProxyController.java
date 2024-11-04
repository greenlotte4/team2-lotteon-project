package com.lotteon.controller.apicontroller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class ProxyController {

    private final RestTemplate restTemplate;

    private final String SERVICE_KEY = "c2FuZ2h1bjExMDEwODhAZ21haWwuY29t";
    private final String API_URL = "https://bizno.net/api/fapi";

    @GetMapping("/api/bizno")
    public String getBiznoInfo(@RequestParam String brno) {
        String url = String.format("%s?key=%s&gb=%d&q=%s&type=%s", API_URL, SERVICE_KEY, 1, brno, "json");
        System.out.println(restTemplate.getForObject(url, String.class).toString());
        return restTemplate.getForObject(url, String.class);
    }
}
