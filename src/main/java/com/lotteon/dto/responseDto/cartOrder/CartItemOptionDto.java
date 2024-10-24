package com.lotteon.dto.responseDto.cartOrder;

import com.lotteon.entity.product.Product;
import jakarta.persistence.*;
import lombok.ToString;

public class CartItemOptionDto {

    private Long id;
    private String optionName; // 예: "사이즈", "색상"
    private String optionValue; // 예: "S", "M", "L"
    private Double additionalPrice; // 추가 가격 (옵션 선택 시 가격 증가분)

}
