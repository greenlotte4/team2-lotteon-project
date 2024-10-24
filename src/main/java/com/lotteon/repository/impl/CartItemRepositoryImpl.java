package com.lotteon.repository.impl;

import com.lotteon.entity.product.QCartItem;
import com.lotteon.repository.custom.CartItemRepositoryCustom;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Repository
public class CartItemRepositoryImpl implements CartItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCartItem cartItem = QCartItem.cartItem;

    @Override
    public Long deleteCartItemsByCartItemId(List<Long> cartItemIds) {

        Long deletedCount = queryFactory
                .delete(QCartItem.cartItem)
                .where(cartItem.id.in(cartItemIds))
                .execute();

        return deletedCount;
    }
}
