package com.lotteon.controller.controller;

import com.lotteon.dto.requestDto.*;
import com.lotteon.dto.responseDto.GetCategoryDto;
import com.lotteon.dto.responseDto.ProductPageResponseDTO;
import com.lotteon.entity.product.ProductOption;
import com.lotteon.service.category.CategoryProductService;
import com.lotteon.service.product.ProductDetailService;
import com.lotteon.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/prod")
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    private final CategoryProductService categoryProductService;
    private final ProductService productService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductDetailService productDetailService;

    @GetMapping("/products")
    public String products(Model model, @RequestParam(value = "cate",required = false) String cate, ProductPageRequestDTO productPageRequestDTO) {
        log.info("123123123"+cate);

        List<GetCategoryDto> navigation = null;

        ProductPageResponseDTO<PostProductDTO> products = categoryProductService.findProductCategory(cate, productPageRequestDTO);

        List<GetCategoryDto> category1 = categoryProductService.findCategory();
        model.addAttribute("products", products);
        model.addAttribute("category1", category1);
        model.addAttribute("cate", cate);

        String setSortType = productPageRequestDTO.getSort();
        model.addAttribute("setSortType", setSortType);

        return "pages/product/list";
    }

    @GetMapping("/product")
    public String product(Model model, @RequestParam(value = "prodId",required = false) long prodId) {

        PostProductDTO postProductDTO = productService.selectProduct(prodId);
        List<PostProductOptionDTO> options = productService.findOption(prodId);
        List<GetCategoryDto> category1 = categoryProductService.findCategory();
        PostProdDetailDTO prodDetail = productDetailService.selectProdDetail(prodId);
        Set<String> addedOptions = new HashSet<>();

        model.addAttribute("addedOptions", addedOptions);
        model.addAttribute("prodDetail", prodDetail);
        model.addAttribute("options", options);
        model.addAttribute("product", postProductDTO);
        log.info("4444"+postProductDTO);
        model.addAttribute("category1", category1);
        return "pages/product/view";
    }


    @GetMapping("/products/search")
    public String search(
            Model model,
            @RequestParam(value = "search",defaultValue = "0") String search,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "searchType",defaultValue = "0")String searchType,
            @RequestParam(value = "keyword",defaultValue = "0") String keyword,
            @RequestParam(value = "sortby",defaultValue = "0") String sortBy,
            @RequestParam(value = "min",defaultValue = "0") int min,
            @RequestParam(value = "max",defaultValue = "0") int max,
            @RequestParam(value = "related",defaultValue = "0") String related
    ) {
        List<GetCategoryDto> category1 = categoryProductService.findCategory();
        model.addAttribute("category1", category1);
        if (!related.equals("0")) {
            HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();

            // 해시에서 현재 검색어의 카운트를 가져옵니다.
            Integer currentCount = hashOps.get(related, search);

            if (currentCount != null) {
                // 존재하면 카운트를 +1 증가
                hashOps.put(related, search, currentCount + 1);
            } else {
                // 존재하지 않으면 1로 설정
                hashOps.put(related, search, 1);
            }
        }
        Page<GetProductDto> products;
        if(searchType.equals("0")){
            products = productService.searchProducts(page,search,sortBy);
        } else {
            products = productService.searchProductsAndDetailSearch(page,search,sortBy,keyword,searchType,min,max);
        }

        model.addAttribute("products",products);
        model.addAttribute("page",page);
        model.addAttribute("search",search);
        model.addAttribute("totalPages",products.getTotalPages());
        model.addAttribute("keyword",keyword);
        model.addAttribute("searchType",searchType);
        model.addAttribute("sortBy",sortBy);
        model.addAttribute("totalCnt",products.getTotalElements());
        model.addAttribute("min",min);
        model.addAttribute("max",max);
        model.addAttribute("related",search);
        return "pages/product/search";
    }

}
