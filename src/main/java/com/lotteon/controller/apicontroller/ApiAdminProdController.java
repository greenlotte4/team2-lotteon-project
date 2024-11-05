package com.lotteon.controller.apicontroller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostProdAllDTO;
import com.lotteon.dto.requestDto.PostProductOptionDTO;
import com.lotteon.dto.requestDto.cartOrder.PostOrderDeliDto;
import com.lotteon.dto.responseDto.GetCategoryDto;
import com.lotteon.dto.responseDto.GetDeliInfoDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderDto;
import com.lotteon.entity.product.Product;
import com.lotteon.service.category.CategoryProductService;
import com.lotteon.service.product.OrderItemService;
import com.lotteon.service.product.OrderService;
import com.lotteon.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/admin/prod")
@RequiredArgsConstructor
public class ApiAdminProdController {

    private final ProductService productService;
    private final CategoryProductService categoryProductService;
    private final OrderItemService orderItemService;
    private final OrderService orderService;

    @PostMapping("/info")
    public ResponseEntity<Map<String, Object>> info(@ModelAttribute PostProdAllDTO postProdAllDTO) {

        Map<String, Object> response = new HashMap<>();

        if(Objects.equals(postProdAllDTO.getType(), "insert")){
            // date를 timestamp로 바꿔서 집어넣는 과정
            LocalDateTime localDateTime = postProdAllDTO.getPostProdDetailDTO().getMDate1().atStartOfDay();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            postProdAllDTO.getPostProdDetailDTO().setMdate(timestamp);

            // 디테일이랑 카테고리 insert 하기전에 productId 넣어주는 작업
            Product result = productService.insertProduct(postProdAllDTO.getPostProductDTO(), postProdAllDTO.getPostProdDetailDTO());
            postProdAllDTO.getPostProdCateMapperDTO().setProductId(result.getId());
            postProdAllDTO.getPostProdDetailDTO().setProductId(result.getId());

            productService.insertProdDetail(postProdAllDTO.getPostProdDetailDTO());
            categoryProductService.insertCateMapper(postProdAllDTO.getPostProdCateMapperDTO());
            response.put("success", result.getId());
        }else if(Objects.equals(postProdAllDTO.getType(), "modify")){
            log.info("444444444444444444444"+postProdAllDTO.getPostProdCateMapperDTO());
            LocalDateTime localDateTime = postProdAllDTO.getPostProdDetailDTO().getMDate1().atStartOfDay();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            postProdAllDTO.getPostProdDetailDTO().setMdate(timestamp);

            Product product = productService.updateProduct(postProdAllDTO.getPostProductDTO(), postProdAllDTO.getPostProdDetailDTO(), postProdAllDTO.getProdId());
            categoryProductService.updateCateMapper(postProdAllDTO.getPostProdCateMapperDTO(), product);
        }


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

        int total = 0;
        for(PostProductOptionDTO optionDTO : optionDTOS) {
            log.info("444545455545454545454545"+optionDTO);
            total += productService.insertProdOption(optionDTO);

        }
        log.info("total값은?" + total);


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

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> adminOrderDetail(@PathVariable Long orderId, Authentication authentication) {

        MyUserDetails auth2  =(MyUserDetails) authentication.getPrincipal();
        log.info("컨트롤러에서 어드민인지 셀러인지 확인 "+auth2.getUser().getMemRole());
        ResponseOrderDto responseOrderDto = orderItemService.selectAdminOrder(orderId);

        if(responseOrderDto==null ){
            return ResponseEntity.ok().body(false);
        }

        return ResponseEntity.ok().body(responseOrderDto);
    }

    @PostMapping("/order/delivery")
    public ResponseEntity<?> adminOrderDeli(@RequestBody PostOrderDeliDto postOrderDeliDto){
        log.info("배송정보 업데이트 컨트롤러 확인 "+postOrderDeliDto.toString());
        Boolean result = orderService.updateOrderDeli(postOrderDeliDto);
        log.info("컨트롤러에서 서비스 성공했는지 확인 "+result);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/order/delinfo")
    public ResponseEntity<?> adminOrderDeliInfo(@RequestParam String deliveryId){

        GetDeliInfoDto deliInfo = orderService.findByDeliveryId(deliveryId);
        Map<String,Object> map = new HashMap<>();
        map.put("info",deliInfo);
        return ResponseEntity.ok().body(map);
    }
}
