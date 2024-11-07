package com.lotteon.repository.impl;

import com.lotteon.dto.requestDto.ProductPageRequestDTO;
import com.lotteon.entity.member.QSeller;
import com.lotteon.entity.product.QProduct;
import com.lotteon.entity.product.QReview;
import com.lotteon.repository.custom.ProductRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {


    private final JPAQueryFactory queryFactory;
    private final QProduct qProduct = QProduct.product;
    private final QSeller qSeller = QSeller.seller;
    private final QReview qReview = QReview.review;


    @Override
    public Page<Tuple> selectProductAllForList(ProductPageRequestDTO pageRequestDTO, Pageable pageable, long sellId) {

        log.info("123123123123123123123123");
        System.out.println("======================== "+sellId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = null;

        if (authentication != null && authentication.isAuthenticated()) {
            // 권한 목록 가져오기
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            // 권한 정보를 출력하거나 처리
            for (GrantedAuthority authority : authorities) {
                role = authority.getAuthority(); // 권한 또는 등급 정보
                log.info("User Role: {}", role);
            }
        }

        if("ROLE_admin".equals(role)) {

            List<Tuple> content = queryFactory.select(qProduct, qSeller.sellCompany)
                    .from(qProduct)
                    .join(qSeller)
                    .on(qProduct.seller.id.eq(qSeller.id))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(qProduct.id.desc())
                    .fetch();

            log.info("content : "+content);

            long total = queryFactory.select(qProduct.count())
                        .from(qProduct).fetchOne();

            log.info("total : "+total);

            return new PageImpl<Tuple>(content, pageable, total);
        } else if("ROLE_seller".equals(role)) {
            List<Tuple> content = queryFactory.select(qProduct, qSeller.sellCompany)
                .from(qProduct)
                .join(qSeller)
                .on(qProduct.seller.id.eq(qSeller.id))
                .where(qProduct.seller.id.eq(sellId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qProduct.id.desc())
                .fetch();

        log.info("content : "+content);

            long total = queryFactory.select(qProduct.count())
                    .from(qProduct)
                    .join(qSeller)
                    .on(qProduct.seller.id.eq(qSeller.id))
                    .where(qProduct.seller.id.eq(sellId))
                    .fetchOne();

            return new PageImpl<Tuple>(content, pageable, total);
        }

        return null;

    }

    @Override
    public Page<Tuple> selectProductForSearch(ProductPageRequestDTO pageRequestDTO, Pageable pageable, long sellId) {

        String type = pageRequestDTO.getType();
        String keyword = pageRequestDTO.getKeyword();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = null;

        if (authentication != null && authentication.isAuthenticated()) {
            // 권한 목록 가져오기
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            // 권한 정보를 출력하거나 처리
            for (GrantedAuthority authority : authorities) {
                role = authority.getAuthority(); // 권한 또는 등급 정보
                log.info("User Role: {}", role);
            }
        }

        // 검색 선택 조건에 따라 where 조건 표현식 생성
        BooleanExpression expression = null;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qProduct.seller.id.eq(sellId));

        if(type.equals("상품명")){
            builder.and(qProduct.prodName.contains(keyword));
            expression = qProduct.prodName.contains(keyword);
        }else if (type.equals("상품번호")) {
            Long productId = Long.parseLong(keyword);
            builder.and(qProduct.id.eq(productId));
            expression = qProduct.id.eq(productId);
        }else if (type.equals("판매자")) {
            builder.and(qSeller.sellCompany.contains(keyword));
            expression = qSeller.sellCompany.contains(keyword);
        }

        if("ROLE_admin".equals(role)) {
            List<Tuple> content = queryFactory.select(qProduct, qSeller.sellCompany)
                    .from(qProduct)
                    .join(qSeller)
                    .on(qProduct.seller.id.eq(qSeller.id))
                    .where(expression)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(qProduct.id.desc())
                    .fetch();

            BooleanBuilder builder2 = new BooleanBuilder();


            long total = queryFactory.select(qProduct.count())
                    .from(qProduct)
                    .join(qSeller).on(qProduct.seller.id.eq(qSeller.id))
                    .where(expression).fetchOne();

            log.info("impl total : " + total);

            return new PageImpl<Tuple>(content, pageable, total);

        } else if("ROLE_seller".equals(role)) {
            List<Tuple> content = queryFactory.select(qProduct, qSeller.sellCompany)
                    .from(qProduct)
                    .join(qSeller)
                    .on(qProduct.seller.id.eq(qSeller.id))
                    .where(builder)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(qProduct.id.desc())
                    .fetch();

            long total = queryFactory.select(qProduct.count())
                    .from(qProduct).join(qSeller)
                    .on(qProduct.seller.id.eq(qSeller.id)).where(builder).fetchOne();

        return new PageImpl<Tuple>(content, pageable, total);

        }
        return null;
    }

    @Override
    public Page<Tuple> findProductsWithSellerInfoByIds(ProductPageRequestDTO pageRequestDTO, Pageable pageable, List<Long> productIds) {

        String sort = pageRequestDTO.getSort();
        OrderSpecifier<?> orderSpecifier = null;

        if (sort != null && sort.startsWith("prodSold")){
            orderSpecifier = qProduct.prodOrderCnt.desc();
        }else if (sort != null && sort.startsWith("prodLowPrice")) {
            orderSpecifier =  Expressions.numberTemplate(Double.class,
                    "{0} - ({0} * ({1} / 100.0))",
                    qProduct.prodPrice, qProduct.prodDiscount
            ).asc();
        }else if (sort != null && sort.startsWith("prodHighPrice")) {
            orderSpecifier = Expressions.numberTemplate(Double.class,
                    "{0} - ({0} * ({1} / 100.0))",
                    qProduct.prodPrice, qProduct.prodDiscount
            ).desc();
        }else if (sort != null && sort.startsWith("prodScore")) {
            orderSpecifier = qProduct.prodRating.desc();
        }else if (sort != null && sort.startsWith("prodReview")) {
            orderSpecifier = new OrderSpecifier<>(
                    Order.DESC,
                    JPAExpressions
                            .select(qReview.count()) // 카운트 서브쿼리
                            .from(qReview)
                            .where(qReview.product.id.eq(qProduct.id))
            );
        }else if (sort != null && sort.startsWith("prodRdate")) {
            orderSpecifier = qProduct.prodRdate.desc();
        }else if (sort != null && sort.startsWith("prodDiscount")){
            orderSpecifier = qProduct.prodDiscount.desc();
        }else {
            orderSpecifier = qProduct.id.desc();
        }

        // productIds 리스트를 사용하여 Product와 Seller 정보 가져오기
        List<Tuple> content = queryFactory
                .select(qProduct, qSeller.sellCompany, qSeller.sellGrade) // 필요한 컬럼 선택
                .from(qProduct)
                .join(qSeller) // Seller 테이블과 조인
                .on(qProduct.seller.id.eq(qSeller.id)) // 조건: Product의 sellId와 Seller의 id가 같을 때
                .where(qProduct.id.in(productIds)) // productIds 리스트를 사용하여 필터링
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch(); // 쿼리 실행

        long total = queryFactory.select(qProduct.count())
                .from(qProduct).where(qProduct.id.in(productIds)).fetchOne();

        return new PageImpl<Tuple>(content, pageable, total);
    }

}