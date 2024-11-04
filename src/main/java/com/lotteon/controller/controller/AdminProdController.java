package com.lotteon.controller.controller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostProdDetailDTO;
import com.lotteon.dto.requestDto.PostProductDTO;
import com.lotteon.dto.requestDto.PostProductOptionDTO;
import com.lotteon.dto.requestDto.ProductPageRequestDTO;
import com.lotteon.dto.responseDto.*;
import com.lotteon.entity.product.Product;
import com.lotteon.service.category.CategoryProductService;
import com.lotteon.service.product.ProductDetailService;
import com.lotteon.service.product.ProductOptionService;
import com.lotteon.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/prod")
@RequiredArgsConstructor
@Log4j2
public class AdminProdController {
    private final CategoryProductService categoryProductService;
    private final ProductOptionService productOptionService;
    private final ProductService productService;
    private final ProductDetailService productDetailService;
    @ModelAttribute
    public void pageIndex(Model model) {
        model.addAttribute("config",getSideValue());
    }
    private String getSideValue() {
        return "product";  // 실제 config 값을 여기에 설정합니다.
    }

    @GetMapping("/products")
    public String products(Model model, ProductPageRequestDTO productPageRequestDTO) {
        ProductPageResponseDTO<PostProductDTO> products = productService.getPageProductListAdmin(productPageRequestDTO);
        model.addAttribute("products", products);
        model.addAttribute("active","products");
        return "pages/admin/product/list";
    }

    @GetMapping("/product/post")
    public String post(Model model) {
        model.addAttribute("active","product");
        List<GetProdCateDTO> prodCate = categoryProductService.findCateAll();
        model.addAttribute("prodCate", prodCate);
        return "pages/admin/product/register";
    }

    @GetMapping("/category")
    public String cate(Model model) {
        List<GetCategoryDto> cate1 = categoryProductService.findCategory();
        model.addAttribute("cate1", cate1);
        model.addAttribute("active","category");
        return "pages/admin/product/category";
    }

    @GetMapping("/product/modify")
    public String modify(Model model, @RequestParam(value = "prodId",required = false) long prodId) {

        PostProductDTO postProductDTO = productService.selectProduct(prodId);
        model.addAttribute("product", postProductDTO);
        PostProdDetailDTO prodDetail = productDetailService.selectProdDetail(prodId);
        model.addAttribute("prodDetail", prodDetail);
        GetCateLocationDTO getCateLocationDTO = categoryProductService.cateLocation2(prodId);
        model.addAttribute("productCategory", getCateLocationDTO);
        List<PostProductOptionDTO> productOptions = productOptionService.findOptionByProduct(prodId);
        model.addAttribute("options", productOptions);
        List<GetProdCateDTO> prodCate = categoryProductService.findCateAll();
        model.addAttribute("prodCate", prodCate);
        return "pages/admin/product/modify";
    }

}
