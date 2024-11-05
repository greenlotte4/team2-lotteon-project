package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.*;
import com.lotteon.dto.responseDto.GetMainProductDto;
import com.lotteon.dto.responseDto.ProductPageResponseDTO;
import com.lotteon.entity.category.CategoryProductMapper;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.product.*;
import com.lotteon.repository.category.CategoryProdMapperRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.member.SellerRepository;
import com.lotteon.repository.product.OrderRepository;
import com.lotteon.repository.product.ProductDetailRepository;
import com.lotteon.repository.product.ProductOptionRepository;
import com.lotteon.repository.product.ProductRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ModelMapper modelMapper;
    private final SellerRepository sellerRepository;
    private final JPAQueryFactory queryFactory;
    private final OrderRepository orderRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    private final RedisTemplate<String,List<GetMainProductDto>> bestredisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;
    private final CategoryProdMapperRepository categoryProdMapperRepository;

    @Value("${file.upload-dir}")
    private String uploadPath;

    public Product insertProduct(PostProductDTO productDTO, PostProdDetailDTO prodDetailDTO) {

        File fileUploadPath = new File(uploadPath);

        //파일 업로드 디렉터리가 존재하지 않으면 디렉터리 생성
        if (!fileUploadPath.exists()) {
            fileUploadPath.mkdirs();
        }

        //파일 업로드 시스템 경로 구하기
        String path = fileUploadPath.getAbsolutePath();

        List<MultipartFile> prodFiles = new ArrayList<>();  // ArrayList로 초기화
        prodFiles.add(productDTO.getListImage());
        prodFiles.add(productDTO.getBasicImage());
        prodFiles.add(productDTO.getDetailImage());
        prodFiles.add(productDTO.getDescription());

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
                        case 4:
                            prodDetailDTO.setDescription(sName);
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

            MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Seller seller = auth.getUser().getSeller();
            Member member = memberRepository.findBySeller(seller).orElseThrow();
            productDTO.setSeller(seller);
            Product product = modelMapper.map(productDTO, Product.class);
            return productRepository.save(product);
        } else {
            return null;
        }
    }


    public void insertProdDetail(PostProdDetailDTO postProdDetailDTO){

        ProductDetail prodDetail = modelMapper.map(postProdDetailDTO, ProductDetail.class);
        productDetailRepository.save(prodDetail);
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
        long id = ((MyUserDetails) principal).getUser().getSeller().getId();
        System.out.println(id+"==============================================");
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
            PostProductDTO dto = PostProductDTO.builder()
                    .sellId(product.getSeller().getId())
                    .build();
            postProductDTO.setSellId(dto.getSellId());
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
        opt.get().updateViewCnt();

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

    public Page<GetProductDto> searchProducts(int page, String search, String sortBy) {
        Pageable pageable = PageRequest.of(page, 7);
        Page<Product> products;
        Page<GetProductDto> dtos;
        if(!search.equals("0")){
            String redisKey = "search_count"; // ZSet 키
            double incrementValue = 1.0; // 점수로 사용할 값 (1 증가)
            redisTemplate.opsForZSet().incrementScore(redisKey, search, incrementValue);
            redisTemplate.expire(redisKey, 2, TimeUnit.HOURS);
        }

        if(sortBy.equals("0")){
            if(search.equals("0")){
                products = productRepository.findAllByOrderByProdOrderCntDesc(pageable);
            } else {
                products = productRepository.findAllByProdNameContainingOrderByProdOrderCntDesc(search,pageable);
            }
        } else {
            if(search.equals("0")){
                products = this.sortBy(page,sortBy);
            } else {
                products = this.searchAndSortBy(page,search,sortBy);
            }
        }
        dtos = products.map(v->v.toGetProductDto());

        return dtos;
    }

    private Page<Product> sortBy(int page, String sortBy) {
        Pageable pageable = PageRequest.of(page, 7);
        Page<Product> products;
        if(sortBy.equals("order")){
            products = productRepository.findAllByOrderByProdOrderCntDesc(pageable);
        } else if (sortBy.equals("desc")) {
            products = productRepository.findAllByOrderByProdPriceDesc(pageable);
        } else if (sortBy.equals("asc")) {
            products = productRepository.findAllByOrderByProdPriceAsc(pageable);
        } else if (sortBy.equals("rating")) {
            products = productRepository.findAllByOrderByProdRatingDesc(pageable);
        } else if (sortBy.equals("review")) {
            products = productRepository.findAllByOrderByProdReviewCntDesc(pageable);
        } else {
            products = productRepository.findAllByOrderByProdRdateDesc(pageable);
        }
        return products;
    }

    private Page<Product> searchAndSortBy(int page, String search, String sortBy) {
        Pageable pageable = PageRequest.of(page, 7);
        Page<Product> products;
        if(sortBy.equals("order")){
            products = productRepository.findAllByProdNameContainingOrderByProdOrderCntDesc(search,pageable);
        } else if (sortBy.equals("desc")) {
            products = productRepository.findAllByProdNameContainingOrderByProdPriceDesc(search,pageable);
        } else if (sortBy.equals("asc")) {
            products = productRepository.findAllByProdNameContainingOrderByProdPriceAsc(search,pageable);
        } else if (sortBy.equals("rating")) {
            products = productRepository.findAllByProdNameContainingOrderByProdRatingDesc(search,pageable);
        } else if (sortBy.equals("review")) {
            products = productRepository.findAllByProdNameContainingOrderByProdReviewCntDesc(search,pageable);
        } else {
            products = productRepository.findAllByProdNameContainingOrderByProdRdateDesc(search,pageable);
        }
        return products;
    }

    public Page<GetProductDto> searchProductsAndDetailSearch(int page, String search, String sortBy, String keyword, String searchType,int min,int max) {
        Pageable pageable = PageRequest.of(page, 10);
        BooleanBuilder builder = new BooleanBuilder();

        // 검색어가 있을 경우, 추가 조건을 생성합니다.
        if (keyword != null && !keyword.isEmpty()) {
            builder.and(QProduct.product.prodName.containsIgnoreCase(search));
        }

        // searchType 처리
        String[] types = searchType.split("/");
        for (String type : types) {
            switch (type) {
                case "판매자":
                    builder.and(QProduct.product.seller.sellCompany.containsIgnoreCase(keyword)); // 판매자 검색 추가
                    break;
                case "설명":
                    builder.and(QProduct.product.prodSummary.containsIgnoreCase(keyword)); // 설명 검색 추가
                    break;
                case "최소값":
                    if (min != 0) {
                        builder.and(QProduct.product.prodPrice.goe((double)min)); // 최소값 이상 조건 추가
                    }
                    break;
                case "최대값":
                    if (max != 0) {
                        builder.and(QProduct.product.prodPrice.loe((double)max)); // 최대값 이하 조건 추가
                    }
                    break;
            }
        }

        // 정렬 처리
        QProduct product = QProduct.product;
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if ("asc".equalsIgnoreCase(sortBy)) {
            orderSpecifiers.add(product.prodPrice.asc());
        } else if ("desc".equalsIgnoreCase(sortBy)) {
            orderSpecifiers.add(product.prodPrice.desc());
        } else if ("order".equals(sortBy)) {
            orderSpecifiers.add(product.prodOrderCnt.desc());
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            orderSpecifiers.add(product.prodRating.desc());
        } else if ("review".equalsIgnoreCase(sortBy)) {
            orderSpecifiers.add(product.prodReviewCnt.desc());
        } else {
            orderSpecifiers.add(product.prodRdate.desc());
        }

        // 쿼리 실행
        QueryResults<Product> queryResults = queryFactory
                .selectFrom(product)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        // 결과 변환
        List<Product> products = queryResults.getResults();
        long total = queryResults.getTotal();
        Page<Product> products2 = new PageImpl<>(products, pageable, total);
        Page<GetProductDto> dtos = products2.map(v->v.toGetProductDto());
        System.out.println(dtos);
        return dtos;
    }

    public List<GetMainProductDto> findBestItem() {

        List<GetMainProductDto> cachedProducts = bestredisTemplate.opsForValue().get("best_products");
        System.out.println(cachedProducts);
        if (cachedProducts != null) {
            return cachedProducts;
        }

        List<Product> products = productRepository.findTop3ByOrderByProdOrderCntDesc();
        List<GetMainProductDto> dtos = products.stream().map(Product::toGetMainBestDto).collect(Collectors.toList());

        bestredisTemplate.opsForValue().set("best_products", dtos, 2, TimeUnit.HOURS);
        return dtos;
    }

    public void updateBestItems() {
        List<GetMainProductDto> bestProducts = findBestItem();

        // 클라이언트에게 실시간으로 베스트 상품 정보 전송
        messagingTemplate.convertAndSend("/topic/bestProducts", bestProducts);
    }

    public List<GetMainProductDto> findHitItem() {
        List<Product> products = productRepository.findTop4ByOrderByProdViewsDesc();
        List<GetMainProductDto> dtos = products.stream().map(Product::toGetMainHitDto).toList();
        return dtos;
    }

    public List<GetMainProductDto> findRecentItem() {
        List<Product> products = productRepository.findTop4ByOrderByProdRdateDesc();
        List<GetMainProductDto> dtos = products.stream().map(Product::toGetMainHitDto).toList();
        return dtos;
    }

    public List<GetMainProductDto> findRecommendItem() {
        List<Product> products = productRepository.findTop4ByOrderByProdRatingDesc();
        List<GetMainProductDto> dtos = products.stream().map(Product::toGetMainHitDto).toList();
        return dtos;
    }

    public List<GetProductNamesDto> findReviewNames(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        List<Product> products = order.get().getOrderItems().stream().map(v->v.getProduct()).toList();
        List<GetProductNamesDto> dtos = products.stream().map(Product::toGetProductNamesDto).toList();
        return dtos;
    }

    public void top3UpdateBoolean() {
        List<GetMainProductDto> cachedProducts = bestredisTemplate.opsForValue().get("best_products");
        List<Product> products = productRepository.findTop3ByOrderByProdOrderCntDesc();
        List<GetMainProductDto> dtos = products.stream().map(Product::toGetMainBestDto).collect(Collectors.toList());
        if (cachedProducts == null || !cachedProducts.equals(dtos)) {
            bestredisTemplate.opsForValue().set("best_products", dtos, 2, TimeUnit.HOURS);
        }
    }

    public List<GetMainProductDto> findDiscountItem() {
        List<Product> products = productRepository.findTop4ByOrderByProdDiscountDesc();
        List<GetMainProductDto> dtos = products.stream().map(Product::toGetMainHitDto).toList();
        return dtos;
    }

    public List<GetMainProductDto> findSavePointItem() {
        List<Product> products = productRepository.findTop4ByOrderByProdPointDesc();
        List<GetMainProductDto> dtos = products.stream().map(Product::toGetMainHitDto).toList();
        return dtos;
    }

    public Product updateProduct(PostProductDTO productDTO, PostProdDetailDTO prodDetailDTO, long prodId){
        File fileUploadPath = new File(uploadPath);

        //파일 업로드 디렉터리가 존재하지 않으면 디렉터리 생성
        if (!fileUploadPath.exists()) {
            fileUploadPath.mkdirs();
        }

        //파일 업로드 시스템 경로 구하기
        String path = fileUploadPath.getAbsolutePath();

        List<MultipartFile> prodFiles = new ArrayList<>();  // ArrayList로 초기화
        prodFiles.add(productDTO.getListImage());
        prodFiles.add(productDTO.getBasicImage());
        prodFiles.add(productDTO.getDetailImage());
        prodFiles.add(productDTO.getDescription());

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
                            log.info("Setting List Image: " + sName);
                            break;
                        case 2:
                            productDTO.setProdBasicImg(sName);
                            break;
                        case 3:
                            productDTO.setProdDetailImg(sName);
                            break;
                        case 4:
                            prodDetailDTO.setDescription(sName);
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

            Product product = productRepository.findById(prodId).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
            ProductDetail productDetail = productDetailRepository.findByProductId(prodId);
            prodDetailDTO.setProductId(prodId);
            productDetail.updateDetail(prodDetailDTO);
            productDTO.updateSeller(product, prodId);
            product.updateProduct(productDTO);

            return product;

        }else{
            return null;
        }
    }

}
