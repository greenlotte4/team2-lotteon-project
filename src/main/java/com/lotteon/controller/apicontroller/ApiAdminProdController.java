package com.lotteon.controller.apicontroller;

import ch.qos.logback.core.model.Model;
import com.lotteon.dto.requestDto.PostProdAllDTO;
import com.lotteon.dto.requestDto.PostProductDTO;
import com.lotteon.service.category.CategoryProductService;
import com.lotteon.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/admin/prod")
@RequiredArgsConstructor
public class ApiAdminProdController {

    private final ProductService productService;
    private final CategoryProductService categoryProductService;

    @PostMapping("/info")
    public ResponseEntity<Map<String, Object>> info(Model model,@ModelAttribute PostProdAllDTO postProdAllDTO) {


        log.info("124443"+postProdAllDTO.getPostProdDetailDTO());
        log.info("134443"+postProdAllDTO.getPostProductDTO());
        log.info("14444443"+postProdAllDTO.getPostProdCateMapperDTO());

//        productService.insertProduct(postProductDTO);

        categoryProductService.insertProdCate();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);

    }

}
