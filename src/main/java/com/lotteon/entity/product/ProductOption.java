package com.lotteon.entity.product;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_option")
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id")
    private Product product;

    @Column(name = "prod_option_state")
    private int optionState; // 필수옵션인지 아닌지

    @Column(name = "prod_option_name")
    private String optionName; // 예: "사이즈", "색상"
    @Column(name = "prod_option_value")
    private String optionValue; // 예: "S", "M", "L"
    @Column(name = "additionalPrice")
    private Double additionalPrice; // 추가 가격 (옵션 선택 시 가격 증가분)
    @Column(name = "prod_option_stock")
    private int stock;

}
