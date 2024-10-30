package com.lotteon.service.product;

import com.lotteon.dto.requestDto.PostProdDetailDTO;
import com.lotteon.entity.product.ProductDetail;
import com.lotteon.repository.product.ProductDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ProductDetailService {

    private final ProductDetailRepository productDetailRepository;
    private final ModelMapper modelMapper;

    public PostProdDetailDTO selectProdDetail(long id){
       ProductDetail productDetail = productDetailRepository.findByProductId(id);

       return modelMapper.map(productDetail, PostProdDetailDTO.class);
    }

}
