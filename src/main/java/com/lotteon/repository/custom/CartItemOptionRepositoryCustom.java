package com.lotteon.repository.custom;

import java.util.List;

public interface CartItemOptionRepositoryCustom {
    public Long deleteCartItemOptionsByCartItemId(List<Long> cartItemIds);
}
