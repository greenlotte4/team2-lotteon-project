package com.lotteon.controller.controller;

import com.lotteon.dto.requestDto.PostProductDTO;
import com.lotteon.dto.requestDto.PostProductOptionDTO;
import com.lotteon.dto.requestDto.ProductPageRequestDTO;
import com.lotteon.dto.responseDto.GetCategoryDto;
import com.lotteon.dto.responseDto.ProductPageResponseDTO;
import com.lotteon.entity.product.ProductOption;
import com.lotteon.service.category.CategoryProductService;
import com.lotteon.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @GetMapping("/products")
    public String products(Model model, @RequestParam(value = "cate",required = false) String cate, ProductPageRequestDTO productPageRequestDTO) {
        log.info("123123123"+cate);

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

        Set<String> addedOptions = new HashSet<>();

        model.addAttribute("addedOptions", addedOptions);

        model.addAttribute("options", options);
        model.addAttribute("product", postProductDTO);
        log.info("4444"+postProductDTO);
        model.addAttribute("category1", category1);
        return "pages/product/view";
    }


    @GetMapping("/products/search")
    public String search(Model model) {
        List<GetCategoryDto> category1 = categoryProductService.findCategory();

        model.addAttribute("category1", category1);
        return "pages/product/search";
    }

}
