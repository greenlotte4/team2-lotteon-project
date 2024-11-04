package com.lotteon.controller.apicontroller;

import com.lotteon.dto.requestDto.PostAdminSellerDto;
import com.lotteon.service.member.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/shop")
@RequiredArgsConstructor
public class ApiAdminShopController {

    private final SellerService sellerService;

    @PostMapping("/seller")
    public ResponseEntity<?> postSeller(
            @RequestBody PostAdminSellerDto postAdminSellerDto
            ){

        sellerService.postSeller(postAdminSellerDto);


        return ResponseEntity.ok("SU");
    }

    @DeleteMapping("/shop")
    public ResponseEntity<?> deleteShop(
            @RequestParam String id
    ){
        List<Long> ids = Arrays.stream(id.split(","))
                .map(Long::valueOf) // 각 요소를 Long으로 변환
                .collect(Collectors.toList());

        sellerService.delete(ids);
        return ResponseEntity.ok("SU");
    }
}
