package com.lotteon.service.product;

import com.lotteon.dto.requestDto.PostProductDTO;
import com.lotteon.dto.requestDto.PostProductOptionDTO;
import com.lotteon.entity.product.Product;
import com.lotteon.entity.product.ProductOption;
import com.lotteon.repository.product.ProductOptionRepository;
import com.lotteon.repository.product.ProductRepository;
import com.lotteon.service.config.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadPath;

    public Product insertProduct(PostProductDTO productDTO) {

        File fileUploadPath = new File(uploadPath);

        //파일 업로드 디렉터리가 존재하지 않으면 디렉터리 생성
        if(!fileUploadPath.exists()){
            fileUploadPath.mkdirs();
        }

        //파일 업로드 시스템 경로 구하기
        String path = fileUploadPath.getAbsolutePath();

        log.info("pathpathpathpathpathpath :: "+path);

        List<MultipartFile> prodFiles = new ArrayList<>();  // ArrayList로 초기화
        prodFiles.add(productDTO.getListImage());
        prodFiles.add(productDTO.getBasicImage());
        prodFiles.add(productDTO.getDetailImage());

        int i = 1;  // 이미지 번호를 매기기 위한 인덱스
        boolean isUploadSuccessful = true;
        for(MultipartFile file : prodFiles){
            if(!file.isEmpty()){
                // 원본 파일명 가져오기
                String oName = file.getOriginalFilename();
                // 파일 확장자 추출
                String ext = oName.substring(oName.lastIndexOf("."));
                // UUID를 사용하여 새로운 파일명 생성
                String sName = UUID.randomUUID().toString() + ext;

                // 파일 저장
                try {
                    file.transferTo(new File(path, sName));
                    switch (i){
                        case 1:
                            productDTO.setProdListImg(sName);
                            break;
                        case 2:
                            productDTO.setProdBasicImg(sName);
                            break;
                        case 3:
                            productDTO.setProdDetailImg(sName);
                            break;
                    }
                } catch (IOException e) {
                    log.error(e);
                    isUploadSuccessful = false;
                }
            }
            i++;
        }
        if (isUploadSuccessful) {
            Product product = modelMapper.map(productDTO, Product.class);
            log.info("123123123123" + product);
            Product result = productRepository.save(product);
            log.info("result.getID 결과값은??????"+result.getId());
            return result;
        }else {
            return null;
        }
    }

    public void insertProdOption(PostProductOptionDTO optionDTO) {

        Optional<Product> opt = productRepository.findById(optionDTO.getProductId());

        Product product = null;
        if(opt.isPresent()){
            product = opt.get();
        }

        log.info("666767776767" + product);

        ProductOption productOption = ProductOption.builder()
                .product(product)
                .optionName(optionDTO.getOptionName())
                .optionValue(optionDTO.getOptionValue())
                .additionalPrice(optionDTO.getAdditionalPrice())
                .stock(optionDTO.getStock())
                .build();

        productOptionRepository.save(productOption);
    }


}
