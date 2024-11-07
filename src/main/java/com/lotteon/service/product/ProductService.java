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
import com.lotteon.service.config.ImageService;
import com.lotteon.repository.product.*;

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
    private final ImageService imageService;
    private final CategoryProdMapperRepository categoryProdMapperRepository;
    private final OrderItemRepository orderItemRepository;

    @Value("${file.upload-dir}")
    private String uploadPath;

    public Product insertProduct(PostProductDTO productDTO, PostProdDetailDTO prodDetailDTO) {

        if (productDTO.getListImage() != null && !productDTO.getListImage().isEmpty()){
            String uploadedImagePath = imageService.uploadImage(productDTO.getListImage());
            if (uploadedImagePath != null) {
                productDTO.setProdListImg(uploadedImagePath);
            }
        }
        if (productDTO.getBasicImage() != null && !productDTO.getBasicImage().isEmpty()){
            String uploadedImagePath = imageService.uploadImage(productDTO.getBasicImage());
            if (uploadedImagePath != null) {
                productDTO.setProdBasicImg(uploadedImagePath);
            }
        }
        if (productDTO.getDetailImage() != null && !productDTO.getDetailImage().isEmpty()){
            String uploadedImagePath = imageService.uploadImage(productDTO.getDetailImage());
            if (uploadedImagePath != null) {
                productDTO.setProdDetailImg(uploadedImagePath);
            }
        }
        if (productDTO.getDescription() != null && !productDTO.getDescription().isEmpty()){
            String uploadedImagePath = imageService.uploadImage(productDTO.getDescription());
            if (uploadedImagePath != null) {
                prodDetailDTO.setDescription(uploadedImagePath);
            }
        }

        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Seller seller = auth.getUser().getSeller();
        Member member = memberRepository.findBySeller(seller).orElseThrow();
        productDTO.setSeller(seller);
        Product product = modelMapper.map(productDTO, Product.class);
        return productRepository.save(product);

    }


    public void insertProdDetail(PostProdDetailDTO postProdDetailDTO){

        ProductDetail prodDetail = modelMapper.map(postProdDetailDTO, ProductDetail.class);
        productDetailRepository.save(prodDetail);
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
        List<OrderItem> orderItems = orderItemRepository.findByOrder_IdAndState2(orderId,5);
        List<GetProductNamesDto> dtos = orderItems.stream().map(OrderItem::toGetProductNamesDto).toList();
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

        Product product = productRepository.findById(prodId).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        ProductDetail productDetail = productDetailRepository.findByProductId(prodId);

        // 새 이미지가 들어왔을 때는 기존 이미지 삭제하기
        if (productDTO.getListImage() != null && !productDTO.getListImage().isEmpty()){
            String uploadedImagePath = imageService.uploadImage(productDTO.getListImage());
            if (uploadedImagePath != null) {
                File oldFile = new File(uploadPath + "/" + product.getProdListImg());
                if (oldFile.exists()) {
                    boolean result = oldFile.delete();
                    if (result){
                        log.info("List 이미지가 교체되었습니다.");
                    }
                }
                productDTO.setProdListImg(uploadedImagePath);
            }
        }
        if (productDTO.getBasicImage() != null && !productDTO.getBasicImage().isEmpty()){
            String uploadedImagePath = imageService.uploadImage(productDTO.getBasicImage());
            if (uploadedImagePath != null) {
                File oldFile = new File(uploadPath + "/" + product.getProdBasicImg());
                if (oldFile.exists()) {
                    boolean result = oldFile.delete();
                    if (result){
                        log.info("Basic 이미지가 교체되었습니다.");
                    }
                }
                productDTO.setProdBasicImg(uploadedImagePath);
            }
        }
        if (productDTO.getDetailImage() != null && !productDTO.getDetailImage().isEmpty()){
            String uploadedImagePath = imageService.uploadImage(productDTO.getDetailImage());
            if (uploadedImagePath != null) {
                File oldFile = new File(uploadPath + "/" + product.getProdDetailImg());
                if (oldFile.exists()) {
                    boolean result = oldFile.delete();
                    if (result){
                        log.info("Detail 이미지가 교체되었습니다.");
                    }
                }
                productDTO.setProdDetailImg(uploadedImagePath);
            }
        }
        if (productDTO.getDescription() != null && !productDTO.getDescription().isEmpty()){
            String uploadedImagePath = imageService.uploadImage(productDTO.getDescription());
            if (uploadedImagePath != null) {
                File oldFile = new File(uploadPath + "/" + productDetail.getDescription());
                if (oldFile.exists()) {
                    boolean result = oldFile.delete();
                    if (result){
                        log.info("상세정보 이미지가 교체되었습니다.");
                    }
                }
                prodDetailDTO.setDescription(uploadedImagePath);
            }
        }
        // 이미지가 안들어왔을때는 기존 이미지 유지하도록.
        if(prodDetailDTO.getDescription() == null){
            prodDetailDTO.setDescription(productDetail.getDescription());
        }
        if(productDTO.getProdListImg() == null){
            productDTO.setProdListImg(product.getProdListImg());
        }
        if(productDTO.getProdBasicImg() == null){
            productDTO.setProdBasicImg(product.getProdBasicImg());
        }
        if(productDTO.getProdDetailImg() == null){
            productDTO.setProdDetailImg(product.getProdDetailImg());
        }
        prodDetailDTO.setProductId(prodId);
        productDetail.updateDetail(prodDetailDTO);
        productDTO.updateSeller(product, prodId);
        product.updateProduct(productDTO);
        return product;

    }

    public void updateStock(int totalStock, long prodId){
        Product product = productRepository.findById(prodId).orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        product.updateStock(totalStock);

    }

}
