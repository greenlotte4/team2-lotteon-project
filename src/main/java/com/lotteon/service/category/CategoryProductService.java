package com.lotteon.service.category;

import com.lotteon.dto.requestDto.PostProdCateMapperDTO;
import com.lotteon.dto.requestDto.PostProductDTO;
import com.lotteon.dto.requestDto.ProductPageRequestDTO;
import com.lotteon.dto.responseDto.*;
import com.lotteon.entity.category.CategoryProduct;
import com.lotteon.entity.category.CategoryProductMapper;
import com.lotteon.entity.product.Product;
import com.lotteon.entity.product.QProduct;
import com.lotteon.repository.category.CategoryProdMapperRepository;
import com.lotteon.repository.category.CategoryProductRepository;
import com.lotteon.repository.product.ProductRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/*
* 이름 : 이상훈
* 날짜 : 2024-10-26
* 작업내용 : 카테고리 출력 레디스 인메모리디비 사용하기
* */
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CategoryProductService {

    private final CategoryProductRepository categoryProductRepository;
    private final CategoryProdMapperRepository categoryProdMapperRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<?> getProducts() {
        // 카테고리별 상품 뽑는법
        CategoryProduct cate = categoryProductRepository.findById((long) 28).get();
        List<CategoryProductMapper> products = categoryProdMapperRepository.findAllByCategory(cate);
        if (products.isEmpty()) {
            System.out.println("상품없음");
            return null;
        }
        List<Product> productList = products.stream().map(CategoryProductMapper::getProduct).toList();
        System.out.println(productList);


        return null;
    }

    public void findAllChildCategories(CategoryProduct parent, List<TestResponseDto> result) {
        List<CategoryProduct> children = categoryProductRepository.findByParent(parent);
        for (CategoryProduct child : children) {
            result.add(child.toDto()); // DTO 변환하여 결과 리스트에 추가
            findAllChildCategories(child, result); // 하위 카테고리에 대해 재귀 호출
        }
    }

    @Cacheable(value = "categoryCache", key = "'category'", cacheManager = "cacheManager")
    public List<GetCategoryDto> findCategory() {
        System.out.println("디비접속후 카테고리 조회");
        List<CategoryProduct> categoryProducts = categoryProductRepository.findAllByCategoryLevel(1);
        List<GetCategoryDto> dtos = categoryProducts.stream().map(v -> v.toGetCategoryDto()).collect(Collectors.toList());

        return dtos;
    }

    @Cacheable(value = "categoryCache", key = "'category2_' + #id", cacheManager = "cacheManager")
    public List<GetCategoryDto> findCategory2(Long id) {
        CategoryProduct categoryProducts = categoryProductRepository.findByCategoryId(id);
        List<CategoryProduct> cates = categoryProducts.getChildren();

        List<GetCategoryDto> dtos = cates.stream()
                .sorted(Comparator.comparing(CategoryProduct::getCategoryOrder))
                .map(CategoryProduct::toGetCategoryDto)
                .collect(Collectors.toList());

        return dtos;
    }

    @Cacheable(value = "categoryCache", key = "'category3_' + #id", cacheManager = "cacheManager")
    public Map<String, Object> findCategory3(Long id) {
        CategoryProduct categoryProducts = categoryProductRepository.findByCategoryId(id);
        List<CategoryProduct> cates = categoryProducts.getChildren();
        Map<String, Object> map = new HashMap<>();
        cates.forEach(v -> {

            List<CategoryProduct> cates2 = v.getChildren();
            List<GetCategoryDto> dtos = cates2.stream().map(v2 -> v2.toGetCategoryDto()).collect(Collectors.toList());
            map.put(v.getCategoryName(), dtos);
        });


        return map;
    }

    @CacheEvict(value = "categoryCache", allEntries = true)
    public String deleteCategory(Long id) {
        CategoryProduct categoryProducts = categoryProductRepository.findByCategoryId(id);
        if (categoryProducts.getProductMappings().isEmpty()) {
            categoryProductRepository.deleteById(id);
            return "SU";
        } else {
            return "FA";
        }
    }

    @CacheEvict(value = "categoryCache", allEntries = true)
    public void insertProdCate() {
        Product product = productRepository.findById((long) 1).orElse(null);
        List<CategoryProduct> categoryProduct1 = categoryProductRepository.findAllByCategoryId((long) 28);
        categoryProduct1.stream().forEach(v -> System.out.println(v.getParent().getCategoryId()));

//        categoryProdMapperRepository.save(categoryProduct);

    }

    @CacheEvict(value = "categoryCache", allEntries = true)
    public void postCategory(GetCategoryDto getCategoryDto) {
        Optional<CategoryProduct> categoryProduct = categoryProductRepository.findById(getCategoryDto.getId());

        if (categoryProduct.isEmpty()) {
            return;
        }

        List<CategoryProduct> children = categoryProduct.get().getChildren();

        int newCategoryOrder = children.isEmpty()
                ? 1
                : children.get(children.size() - 1).getCategoryOrder() + 1;

        CategoryProduct newCategoryProduct = CategoryProduct.builder()
                .categoryLevel(categoryProduct.get().getCategoryLevel() + 1)
                .categoryName(getCategoryDto.getName())
                .categoryOrder(newCategoryOrder)
                .parent(categoryProduct.get())
                .build();

        categoryProductRepository.save(newCategoryProduct);

    }

    @CacheEvict(value = "categoryCache", allEntries = true)
    public void putCategory(GetCategoryDto getCategoryDto) {
        Optional<CategoryProduct> categoryProduct = categoryProductRepository.findById(Long.parseLong(getCategoryDto.getName()));
        Optional<CategoryProduct> categoryParent = categoryProductRepository.findById(getCategoryDto.getId());
        if (categoryProduct.get().getCategoryLevel() > categoryParent.get().getCategoryLevel()) {
            categoryProduct.get().updateParent(categoryParent.get());
        } else if (categoryProduct.get().getCategoryLevel() == categoryParent.get().getCategoryLevel()
                && categoryProduct.get().getParent() == categoryParent.get().getParent()
        ) {
            categoryProduct.get().replaceOrder(categoryParent.get());
        }


    }

    public List<GetProdCateDTO> findCateAll() {
        List<CategoryProduct> categoryProducts = categoryProductRepository.findAll();

        System.out.println("123333322" + categoryProducts);

        return categoryProducts
                .stream()
                .map(product -> modelMapper.map(product, GetProdCateDTO.class))
                .toList();
    }

    public void insertCateMapper(PostProdCateMapperDTO postProdCateMapperDTO) {

        Optional<Product> opt = productRepository.findById(postProdCateMapperDTO.getProductId());

        Product product = null;

        if (opt.isPresent()) {
            product = opt.get();
        }

        CategoryProductMapper categoryProductMapper1 = CategoryProductMapper.builder()
                .id(postProdCateMapperDTO.getId())
                .category(categoryProductRepository.findByCategoryId(postProdCateMapperDTO.getCategoryId1()))
                .product(product)
                .build();
        CategoryProductMapper categoryProductMapper2 = CategoryProductMapper.builder()
                .id(postProdCateMapperDTO.getId())
                .category(categoryProductRepository.findByCategoryId(postProdCateMapperDTO.getCategoryId2()))
                .product(product)
                .build();
        CategoryProductMapper categoryProductMapper3 = CategoryProductMapper.builder()
                .id(postProdCateMapperDTO.getId())
                .category(categoryProductRepository.findByCategoryId(postProdCateMapperDTO.getCategoryId3()))
                .product(product)
                .build();

        categoryProdMapperRepository.save(categoryProductMapper1);
        categoryProdMapperRepository.save(categoryProductMapper2);
        categoryProdMapperRepository.save(categoryProductMapper3);
    }

    public ProductPageResponseDTO<PostProductDTO> findProductCategory(String cate, ProductPageRequestDTO pageRequestDTO) {
        Optional<CategoryProduct> opt = categoryProductRepository.findById(Long.parseLong(cate));
        CategoryProduct categoryProduct1 = null;
        if (opt.isPresent()) {
            categoryProduct1 = opt.get();
        }
        List<CategoryProductMapper> cateMappers = categoryProdMapperRepository.findAllByCategory(categoryProduct1);
        // Step 1: cateMappers에서 productId 리스트 추출
        List<Long> productIds = cateMappers.stream()
                .map(mapper -> mapper.getProduct().getId())
                .toList();

        Pageable pageable = pageRequestDTO.getPageable("id");

        Page<Tuple> pageProduct = productRepository.findProductsWithSellerInfoByIds(pageRequestDTO, pageable, productIds);

        List<String> companys = new ArrayList<>();
        List<Integer> grade = new ArrayList<>();
        List<PostProductDTO> productList = pageProduct.getContent().stream().map(tuple -> {
            Product product = tuple.get(0, Product.class);
            String company = tuple.get(1, String.class);
            Integer sellerGrade = tuple.get(2, Integer.class);
            companys.add(company);
            grade.add(sellerGrade);
            return modelMapper.map(product, PostProductDTO.class);
        }).toList();

        for (int i = 0; i < companys.size(); i++) {
            productList.get(i).setSellCompany(companys.get(i));
            productList.get(i).setSellGrade(grade.get(i));
        }

        for (PostProductDTO productDTO : productList) {
            productDTO.setTotalPrice(productDTO.getProdPrice() - productDTO.getProdPrice() * productDTO.getProdDiscount() / 100);
        }

        int total = (int) pageProduct.getTotalElements();

        log.info("11111111111111111111" + productList);

        return ProductPageResponseDTO.<PostProductDTO>builder()
                .productPageRequestDTO(pageRequestDTO)
                .dtoList(productList)
                .total(total)
                .build();

    }

    public GetCateLocationDTO cateLocation(long id) {
        CategoryProduct cateLevel1 = categoryProductRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 카테고리가 없습니다."));
        CategoryProduct cateLevel2 = categoryProductRepository.findById(cateLevel1.getParent().getCategoryId()).orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 카테고리가 없습니다."));
        CategoryProduct cateLevel3 = categoryProductRepository.findById(cateLevel2.getParent().getCategoryId()).orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 카테고리가 없습니다."));

        return GetCateLocationDTO.builder()
                .level1Name(cateLevel1.getCategoryName())
                .level2Name(cateLevel2.getCategoryName())
                .level3Name(cateLevel3.getCategoryName())
                .build();

    }

    public GetCateLocationDTO cateLocation2(long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 카테고리가 없습니다."));
        List<CategoryProductMapper> categoryProductMapper = categoryProdMapperRepository.findAllByProduct(product);

        log.info("CategoryMapper::::" + categoryProductMapper);

        CategoryProductMapper cate1 = null;
        for (CategoryProductMapper cate : categoryProductMapper) {
            cate1 = cate;
        }

        log.info("cateMaper::::" + cate1);

        return GetCateLocationDTO.builder()
                .level1Name(cate1.getCategory().getParent().getParent().getCategoryName())
                .level2Name(cate1.getCategory().getParent().getCategoryName())
                .level3Name(cate1.getCategory().getCategoryName())
                .build();
    }

    public void updateCateMapper(PostProdCateMapperDTO postProdCateMapperDTO, Product product) {

        List<CategoryProductMapper> cates = categoryProdMapperRepository.findAllByProduct(product);

        if (cates.size() >= 3) {
            cates.get(0).categoryUpdate(categoryProductRepository.findByCategoryId(postProdCateMapperDTO.getCategoryId1()));
            cates.get(1).categoryUpdate(categoryProductRepository.findByCategoryId(postProdCateMapperDTO.getCategoryId2()));
            cates.get(2).categoryUpdate(categoryProductRepository.findByCategoryId(postProdCateMapperDTO.getCategoryId3()));

            // 변경된 내용을 저장
            categoryProdMapperRepository.saveAll(cates);


            log.info("56666666666666666" + cates);
        }
    }
}
