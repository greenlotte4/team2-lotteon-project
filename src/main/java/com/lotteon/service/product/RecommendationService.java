package com.lotteon.service.product;

import com.lotteon.dto.requestDto.cartOrder.PostCartSaveDto;
import com.lotteon.entity.product.Cart;
import com.lotteon.entity.product.Product;
import com.lotteon.repository.product.CartRepository;
import com.lotteon.repository.product.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final MongoTemplate mongoTemplate;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;

    public List<Product> findRelatedProducts(Long prodId) {
        // 1단계: prodId를 본 고객 찾기
        Aggregation getCustIdsAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("prodId").is(prodId)),
                Aggregation.group("custId") // 고객 ID로 그룹화
        );

        // 해당 prodId를 본 고객 ID 리스트를 가져옵니다.
        List<Long> custIds = mongoTemplate.aggregate(getCustIdsAggregation, "user_logs", Document.class)
                .getMappedResults().stream()
                .map(doc -> doc.getLong("_id")) // 고객 ID를 추출
                .collect(Collectors.toList());

        // 2단계: 해당 고객들이 상호작용한 다른 상품 ID 집계
        Aggregation relatedProductsAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("custId").in(custIds)), // 특정 고객들만 필터링
                Aggregation.group("prodId").count().as("interactionCount"), // 상품별 카운트
                Aggregation.sort(Sort.by("interactionCount").descending()), // 상호작용 횟수로 정렬
                Aggregation.limit(5) // 상위 5개 상품
        );

        List<Long> relatedProductIds = mongoTemplate.aggregate(relatedProductsAggregation, "user_logs", Document.class)
                .getMappedResults().stream()
                .map(doc -> doc.getLong("_id")) // 연관 상품 ID 추출
                .filter(id -> !id.equals(prodId))
                .collect(Collectors.toList());

        return productRepository.findAllByIdIn(relatedProductIds);
    }

    public List<Product> findRelatedCart(Cart cart) {
        List<Long> prodIds = cart.getItems().stream().map(v->v.getProduct().getId()).toList();
        return this.recommendRelatedCart(prodIds);
    }

    private List<Product> recommendRelatedCart(List<Long> prodIds) {
        // Step 1: Get all `custId`s who interacted with the given `prodIds` in the "cart" action
        Aggregation getCustIdsAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("prodId").in(prodIds)), // Filter by prodIds
                Aggregation.match(Criteria.where("action").is("cart")), // Only "cart" actions
                Aggregation.group("custId") // Group by custId
        );

        List<Long> custIds = mongoTemplate.aggregate(getCustIdsAggregation, "user_logs", Document.class)
                .getMappedResults().stream()
                .map(doc -> doc.getLong("_id")) // Extract custId from the grouped result
                .collect(Collectors.toList());

        // Step 2: Aggregate related products for these custIds excluding the original prodIds
        Aggregation relatedCartsAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("custId").in(custIds)), // Filter logs by custIds
                Aggregation.match(Criteria.where("action").is("cart")), // Only "cart" actions
                Aggregation.match(Criteria.where("prodId").nin(prodIds)), // Exclude original prodIds
                Aggregation.group("prodId").count().as("interactionCount"), // Group by prodId and count occurrences
                Aggregation.sort(Sort.by(Sort.Order.desc("interactionCount"))), // Sort by count in descending order
                Aggregation.limit(3) // Limit to top 5 most interacted products
        );

        // Step 3: Get the related product IDs
        List<Long> relatedProductIds = mongoTemplate.aggregate(relatedCartsAggregation, "user_logs", Document.class)
                .getMappedResults().stream()
                .map(doc -> doc.getLong("_id")) // Extract prodId from the grouped result
                .collect(Collectors.toList());


        return productRepository.findAllByIdIn(relatedProductIds);
    }

    public List<Product> findRelatedOrder(List<PostCartSaveDto> selectedProducts) {
        List<Long> prodIds = selectedProducts.stream().map(PostCartSaveDto::getProductId).toList();
        return this.recommendRelatedOrder(prodIds);
    }

    private List<Product> recommendRelatedOrder(List<Long> prodIds) {
        // Step 1: Get all `custId`s who interacted with the given `prodIds` in the "cart" action
        Aggregation getCustIdsAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("prodId").in(prodIds)), // Filter by prodIds
                Aggregation.match(Criteria.where("action").is("order")), // Only "cart" actions
                Aggregation.group("custId") // Group by custId
        );

        List<Long> custIds = mongoTemplate.aggregate(getCustIdsAggregation, "user_logs", Document.class)
                .getMappedResults().stream()
                .map(doc -> doc.getLong("_id")) // Extract custId from the grouped result
                .collect(Collectors.toList());

        // Step 2: Aggregate related products for these custIds excluding the original prodIds
        Aggregation relatedCartsAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("custId").in(custIds)), // Filter logs by custIds
                Aggregation.match(Criteria.where("action").is("order")), // Only "cart" actions
                Aggregation.match(Criteria.where("prodId").nin(prodIds)), // Exclude original prodIds
                Aggregation.group("prodId").count().as("interactionCount"), // Group by prodId and count occurrences
                Aggregation.sort(Sort.by(Sort.Order.desc("interactionCount"))), // Sort by count in descending order
                Aggregation.limit(5) // Limit to top 5 most interacted products
        );

        // Step 3: Get the related product IDs
        List<Long> relatedProductIds = mongoTemplate.aggregate(relatedCartsAggregation, "user_logs", Document.class)
                .getMappedResults().stream()
                .map(doc -> doc.getLong("_id")) // Extract prodId from the grouped result
                .collect(Collectors.toList());


        return productRepository.findAllByIdIn(relatedProductIds);
    }

}