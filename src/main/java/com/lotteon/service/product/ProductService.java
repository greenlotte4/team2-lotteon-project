package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostProductDTO;
import com.lotteon.dto.requestDto.PostProductOptionDTO;
import com.lotteon.dto.requestDto.ProductPageRequestDTO;
import com.lotteon.dto.responseDto.ProductPageResponseDTO;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.product.Product;
import com.lotteon.entity.product.ProductOption;
import com.lotteon.repository.member.SellerRepository;
import com.lotteon.repository.product.ProductOptionRepository;
import com.lotteon.repository.product.ProductRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ModelMapper modelMapper;
    private final SellerRepository sellerRepository;
    @Value("${file.upload-dir}")
    private String uploadPath;

    public Product insertProduct(PostProductDTO productDTO) {

        File fileUploadPath = new File(uploadPath);

        //파일 업로드 디렉터리가 존재하지 않으면 디렉터리 생성
        if (!fileUploadPath.exists()) {
            fileUploadPath.mkdirs();
        }

        //파일 업로드 시스템 경로 구하기
        String path = fileUploadPath.getAbsolutePath();

        log.info("pathpathpathpathpathpath :: " + path);

        List<MultipartFile> prodFiles = new ArrayList<>();  // ArrayList로 초기화
        prodFiles.add(productDTO.getListImage());
        prodFiles.add(productDTO.getBasicImage());
        prodFiles.add(productDTO.getDetailImage());

        int i = 1;  // 이미지 번호를 매기기 위한 인덱스
        boolean isUploadSuccessful = true;
        for (MultipartFile file : prodFiles) {
            if (!file.isEmpty()) {
                // 원본 파일명 가져오기
                String oName = file.getOriginalFilename();
                // 파일 확장자 추출
                String ext = oName.substring(oName.lastIndexOf("."));
                // UUID를 사용하여 새로운 파일명 생성
                String sName = UUID.randomUUID().toString() + ext;

                // 파일 저장
                try {
                    file.transferTo(new File(path, sName));
                    switch (i) {
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
            log.info("result.getID 결과값은??????" + result.getId());
            return result;
        } else {
            return null;
        }
    }

    public int insertProdOption(PostProductOptionDTO optionDTO) {

        Optional<Product> opt = productRepository.findById(optionDTO.getProductId());

        Product product = null;
        if (opt.isPresent()) {
            product = opt.get();
        }

        log.info("666767776767" + product);

        ProductOption productOption = ProductOption.builder()
                .product(product)
                .optionName(optionDTO.getOptionName())
                .optionValue(optionDTO.getOptionValue())
                .optionName2(optionDTO.getOptionName2())
                .optionValue2(optionDTO.getOptionValue2())
                .optionName3(optionDTO.getOptionName3())
                .optionValue3(optionDTO.getOptionValue3())
                .additionalPrice(optionDTO.getAdditionalPrice())
                .stock(optionDTO.getStock())
                .build();


        ProductOption option = productOptionRepository.save(productOption);
        return option.getStock();


    }

    public ProductPageResponseDTO<PostProductDTO> getPageProductListAdmin(ProductPageRequestDTO pageRequestDTO) {

        // 접속한 사람의 ID값 받아오기 (customer일경우 전부, seller일 경우 자신의 상품만)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        long id = Integer.parseInt(((MyUserDetails) principal).getName());

        Pageable pageable = pageRequestDTO.getPageable("id");
        Page<Tuple> pageProduct = null;
        if(pageRequestDTO.getKeyword() == null) {
            pageProduct = productRepository.selectProductAllForList(pageRequestDTO, pageable, id);
        }else {
            pageProduct = productRepository.selectProductForSearch(pageRequestDTO, pageable, id);
        }

        List<String> companys = new ArrayList<>();
        List<PostProductDTO> productList = pageProduct.getContent().stream().map(tuple -> {
            Product product = tuple.get(0, Product.class);
            String company = tuple.get(1, String.class);
            companys.add(company);
            return modelMapper.map(product, PostProductDTO.class);
        }).toList();

        for(int i = 0; i < companys.size(); i++) {
            productList.get(i).setSellCompany(companys.get(i));
        }

        int total = (int)pageProduct.getTotalElements();

        return ProductPageResponseDTO.<PostProductDTO>builder()
                .productPageRequestDTO(pageRequestDTO)
                .dtoList(productList)
                .total(total)
                .build();
    }

    public PostProductDTO selectProduct(long id) {
        Optional<Product> opt = productRepository.findById(id);
        PostProductDTO postProductDTO = null;
        if (opt.isPresent()) {
            Product product = opt.get();
            postProductDTO = modelMapper.map(product, PostProductDTO.class);
        }

        Optional<Seller> opt2 = sellerRepository.findById(postProductDTO.getSellId());

        if(opt2.isPresent()) {
            Seller seller = opt2.get();
            postProductDTO.setSellCompany(seller.getSellCompany());
            postProductDTO.setSellGrade(seller.getSellGrade());
        }

        double total = postProductDTO.getProdPrice() - postProductDTO.getProdPrice() * (postProductDTO.getProdDiscount()/100);
        log.info("123432114455" + total);
        postProductDTO.setTotalPrice(total);

        return postProductDTO;
    }

    public void deleteProduct(long id){
        productRepository.deleteById(id);
    }

    public List<PostProductOptionDTO> findOption(long id) {

        List<ProductOption> options = productOptionRepository.findByProductId(id);

        List<PostProductOptionDTO> optionDTOs = options.stream()
                .map(option -> modelMapper.map(option, PostProductOptionDTO.class))
                .toList();

        return optionDTOs;
    }

}
