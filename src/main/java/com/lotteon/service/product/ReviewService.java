package com.lotteon.service.product;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostReviewDto;
import com.lotteon.dto.responseDto.GetReviewsDto;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.point.Point;
import com.lotteon.entity.product.Product;
import com.lotteon.entity.product.ReviewDocu;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.point.PointRepository;
import com.lotteon.repository.product.OrderRepository;
import com.lotteon.repository.product.ProductRepository;
import com.lotteon.repository.product.ReviewDocuRepository;
import com.lotteon.service.member.CustomerService;
import com.lotteon.service.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewDocuRepository reviewDocuRepository;
    private final PointService pointService;
    private final PointRepository pointRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public String addReview(PostReviewDto dto) {
        Optional<Product> product = productRepository.findById(dto.getProdId());
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        boolean hasOrdered = orderRepository.existsByCustomerAndOrderItems_Product(customer, product.get());

        if (!hasOrdered) {
            return "상품을 구매한 사용자만 리뷰를 작성할 수 있습니다.";
        }

        ReviewDocu review = ReviewDocu.builder()
                .reviewScore(dto.getScore())
                .custId(customer.getId())
                .memUid(customer.getMember().getMemUid())
                .prodId(dto.getProdId())
                .prodImg(product.get().getProdListImg())
                .reviewContent(dto.getReview())
                .reviewRdate(LocalDateTime.now())
                .prodName(product.get().getProdName())
                .build();
        reviewDocuRepository.save(review);

        List<ReviewDocu> reviews = reviewDocuRepository.findByProdId(dto.getProdId());
        double averageScore = reviews.stream()
                .mapToInt(ReviewDocu::getReviewScore)
                .average()
                .orElse(0.0);

        product.get().updateRating(averageScore);
        productRepository.save(product.get());

        this.updatePoint(customer);

        return "리뷰가 성공적으로 작성되었습니다.";
    }
    private void updatePoint(Customer customer) {

        LocalDate today = LocalDate.now().plusMonths(2);
        
        Point point = Point.builder()
                .pointVar(50)
                .pointType(1)
                .pointEtc("리뷰작성")
                .customer(customer)
                .pointExpiration(today)
                .build();
        pointRepository.save(point);

        int points = customerService.updateCustomerPoint(customer);
        customer.updatePoint(points);
        customerRepository.save(customer);
    }
    public List<GetReviewsDto> findTop3() {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        List<ReviewDocu> reviews = reviewDocuRepository.findTop3ByCustIdOrderByReviewRdateDesc(customer.getId());
        if(reviews.isEmpty()){
            return null;
        }
        List<GetReviewsDto> dtos = reviews.stream().map(ReviewDocu::toGetReviewsDto).toList();

        return dtos;
    }

    public Page<GetReviewsDto> findAll(int page) {
        Pageable pageable = PageRequest.of(page,10);
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        Page<ReviewDocu> reviews = reviewDocuRepository.findAllByCustIdOrderByReviewRdateDesc(customer.getId(),pageable);
        if(reviews.getTotalElements()==0){
            return null;
        }
        Page<GetReviewsDto> dtos = reviews.map(ReviewDocu::toGetReviewsDto);

        return dtos;
    }

    public Page<GetReviewsDto> findAllByProdId(int page,long prodId) {
        Pageable pageable = PageRequest.of(page,5);
        Page<ReviewDocu> reviews = reviewDocuRepository.findAllByProdIdOrderByReviewRdateDesc(prodId,pageable);
        Page<GetReviewsDto> dtos = reviews.map(ReviewDocu::toGetReviewsDto);
        return dtos;
    }
}
