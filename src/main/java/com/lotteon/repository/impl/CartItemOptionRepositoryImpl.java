package com.lotteon.repository.impl;

import com.lotteon.entity.product.QCartItem;
import com.lotteon.entity.product.QCartItemOption;
import com.lotteon.repository.custom.CartItemOptionRepositoryCustom;
import com.lotteon.repository.custom.CartItemRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Repository
public class CartItemOptionRepositoryImpl implements CartItemOptionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCartItem cartItem = QCartItem.cartItem;
    private final QCartItemOption cartItemOption = QCartItemOption.cartItemOption;


    @Override
    public Long deleteCartItemOptionsByCartItemId(List<Long> cartItemIds) {

        Long deletedOption = queryFactory.delete(QCartItemOption.cartItemOption)
                .where(cartItem.id.in(cartItemIds))
                .execute();

        return deletedOption;
    }
}
