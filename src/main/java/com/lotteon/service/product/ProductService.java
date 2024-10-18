package com.lotteon.service.product;

import com.lotteon.dto.requestDto.PostProductDTO;
import com.lotteon.entity.product.Product;
import com.lotteon.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public void insertProduct(PostProductDTO productDTO) {

        Product product = modelMapper.map(productDTO, Product.class);

        log.info("123123123123" + product);

        productRepository.save(product);
    }


}
