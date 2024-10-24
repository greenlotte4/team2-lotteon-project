package com.lotteon.controller.apicontroller;

import ch.qos.logback.core.model.Model;
import com.lotteon.dto.requestDto.PostProdAllDTO;
import com.lotteon.dto.requestDto.PostProductDTO;
import com.lotteon.dto.requestDto.PostProductOptionDTO;
import com.lotteon.dto.responseDto.GetCategoryDto;
import com.lotteon.entity.product.Product;
import com.lotteon.service.category.CategoryProductService;
import com.lotteon.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/admin/prod")
@RequiredArgsConstructor
public class ApiAdminProdController {

    private final ProductService productService;
    private final CategoryProductService categoryProductService;

    @PostMapping("/info")
    public ResponseEntity<Map<String, Object>> info(@ModelAttribute PostProdAllDTO postProdAllDTO) {

        log.info("124443" + postProdAllDTO.getPostProdDetailDTO());
        log.info("134443" + postProdAllDTO.getPostProductDTO());

        Product result = productService.insertProduct(postProdAllDTO.getPostProductDTO());
        postProdAllDTO.getPostProdCateMapperDTO().setProductId(result.getId());

        categoryProductService.insertCateMapper(postProdAllDTO.getPostProdCateMapperDTO());

        Map<String, Object> response = new HashMap<>();
        response.put("success", result.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cate1")
    public ResponseEntity<List<GetCategoryDto>> cateChoice(@RequestBody GetCategoryDto getCategoryDto) {

//        ResponseEntity<List<Category>>

        log.info("222222222"+getCategoryDto.getId());

        log.info("322323333"+categoryProductService.findCategory2(getCategoryDto.getId()));
        return ResponseEntity.ok(categoryProductService.findCategory2(getCategoryDto.getId()));

    }

    @PostMapping("/option")
    public ResponseEntity<Map<String, Object>> option(@RequestBody PostProductOptionDTO[] optionDTOS) {

        for(PostProductOptionDTO optionDTO : optionDTOS) {
            log.info("444545455545454545454545"+optionDTO);
            productService.insertProdOption(optionDTO);

        }

        return null;
    }

    @PostMapping("/product/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody List<String> ids){

        log.info("9999"+ids.toString());

        for(String id : ids){
            productService.deleteProduct(Long.parseLong(id));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);

        }

}
