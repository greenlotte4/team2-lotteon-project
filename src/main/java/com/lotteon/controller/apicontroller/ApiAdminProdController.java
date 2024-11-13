package com.lotteon.controller.apicontroller;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostProdAllDTO;
import com.lotteon.dto.requestDto.PostProductOptionDTO;
import com.lotteon.dto.requestDto.cartOrder.PostOrderDeliDto;
import com.lotteon.dto.responseDto.GetAdminOrderNameDto;
import com.lotteon.dto.responseDto.GetCategoryDto;
import com.lotteon.dto.responseDto.GetDeliInfoDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderDto;
import com.lotteon.dto.responseDto.cartOrder.ResponseOrderItemDto;
import com.lotteon.entity.product.Product;
import com.lotteon.service.category.CategoryProductService;
import com.lotteon.service.product.OrderItemService;
import com.lotteon.service.product.OrderService;
import com.lotteon.service.product.ProductOptionService;
import com.lotteon.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
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
    private final ProductOptionService productOptionService;

    @PostMapping("/info")
    public ResponseEntity<Map<String, Object>> info(@ModelAttribute PostProdAllDTO postProdAllDTO) {

        Map<String, Object> response = new HashMap<>();

        if(Objects.equals(postProdAllDTO.getType(), "insert")){
            // date를 timestamp로 바꿔서 집어넣는 과정
            LocalDateTime localDateTime = postProdAllDTO.getPostProdDetailDTO().getMDate1().atStartOfDay();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            postProdAllDTO.getPostProdDetailDTO().setMdate(timestamp);

            // 디테일이랑 카테고리 insert 하기전에 productId 넣어주는 작업
            postProdAllDTO.getPostProductDTO().setProdReviewCnt(0);
            Product result = productService.insertProduct(postProdAllDTO.getPostProductDTO(), postProdAllDTO.getPostProdDetailDTO());
            postProdAllDTO.getPostProdCateMapperDTO().setProductId(result.getId());
            postProdAllDTO.getPostProdDetailDTO().setProductId(result.getId());

            productService.insertProdDetail(postProdAllDTO.getPostProdDetailDTO());
            categoryProductService.insertCateMapper(postProdAllDTO.getPostProdCateMapperDTO());
            response.put("success", result.getId());
        }else if(Objects.equals(postProdAllDTO.getType(), "modify")){
            LocalDateTime localDateTime = postProdAllDTO.getPostProdDetailDTO().getMDate1().atStartOfDay();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            postProdAllDTO.getPostProdDetailDTO().setMdate(timestamp);

            Product product = productService.updateProduct(postProdAllDTO.getPostProductDTO(), postProdAllDTO.getPostProdDetailDTO(), postProdAllDTO.getProdId());
            categoryProductService.updateCateMapper(postProdAllDTO.getPostProdCateMapperDTO(), product);
            response.put("success", 0);
        }


        return ResponseEntity.ok(response);
    }

    @PostMapping("/cate1")
    public ResponseEntity<List<GetCategoryDto>> cateChoice(@RequestBody GetCategoryDto getCategoryDto) {
        return ResponseEntity.ok(categoryProductService.findCategory2(getCategoryDto.getId()));
    }

    @PostMapping("/option")
    public void option(@RequestBody PostProductOptionDTO[] optionDTOS) {
        for(PostProductOptionDTO optionDTO : optionDTOS) {
            productOptionService.insertProdOption(optionDTO);
        }
    }

    @PostMapping("/modifyOption")
    public void modifyOption(@RequestBody PostProductOptionDTO[] optionDTOS) {

        int total = 0;
        long prodId = 0;
        String delete = "delete";
        String cancel = "cancel";
        for(PostProductOptionDTO optionDTO : optionDTOS) {
            if(optionDTO.getStock() != null){
                total += optionDTO.getStock();
            }
            if(optionDTO.getId() == 0){
                if(optionDTO.getOptionName() != null){
                    productOptionService.insertProdOption(optionDTO);
                }
            } else{
                productOptionService.updateProdOption(optionDTO);
                prodId = optionDTO.getProductId();
            }
            List<Long> optionIds = optionDTO.getOptionId();
            List<Long> optionIds2 = optionDTO.getOptionId2();
            if(optionIds != null){
                for(Long optionId : optionIds){
                    productOptionService.updateProdOptionState(optionId,delete);
                }
            }
            if(optionIds2 != null){
                for(Long optionId : optionIds2){
                    productOptionService.updateProdOptionState(optionId,cancel);
                }
            }
        }
        productService.updateStock(total, prodId);

    }

    @PostMapping("/product/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody List<String> ids){


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
        Map<String,Object> map = new HashMap<>();
        ResponseOrderDto responseOrderDto = orderItemService.selectAdminOrder(orderId);
        log.info("r개가튼 거 "+responseOrderDto.toString());

        List<GetAdminOrderNameDto> itemNames = orderItemService.selectAdminOrderItem(orderId);
        if(responseOrderDto==null ){
            return ResponseEntity.ok().body(false);
        }
        map.put("order", responseOrderDto);
        map.put("itemNames", itemNames);

        return ResponseEntity.ok().body(map);
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
