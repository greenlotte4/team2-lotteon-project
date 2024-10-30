package com.lotteon.service.product;

import com.lotteon.dto.requestDto.PostProdDetailDTO;
import com.lotteon.repository.product.ProductDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ProductDetailService {

    private final ProductDetailRepository productDetailRepository;


    public void selectProdDetail(long id){
       PostProdDetailDTO postProdDetailDTO = productDetailRepository.findByProductId(id);
    }

}
