package com.lotteon.repository.custom;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CartItemRepositoryCustom {
    public Long deleteCartItemsByCartItemId(List<Long> cartItemIds);
}
