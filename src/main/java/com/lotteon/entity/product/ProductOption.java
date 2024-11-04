package com.lotteon.entity.product;

import com.lotteon.dto.responseDto.GetOption1Dto;
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
    @ToString.Exclude
    private Product product;

    @Column(name = "prod_option_state")
    private int optionState; // 필수옵션인지 아닌지

    @Column(name = "prod_option_name1")
    private String optionName; // 예: "사이즈", "색상"
    @Column(name = "prod_option_value1")
    private String optionValue; // 예: "S", "M", "L"

    @Column(name = "prod_option_name2")
    private String optionName2; // 예: "사이즈", "색상"
    @Column(name = "prod_option_value2")
    private String optionValue2; // 예: "S", "M", "L"

    @Column(name = "prod_option_name3")
    private String optionName3; // 예: "사이즈", "색상"
    @Column(name = "prod_option_value3")
    private String optionValue3; // 예: "S", "M", "L"

    @Column(name = "additionalPrice")
    private Double additionalPrice;

    @Setter
    @Column(name = "prod_option_stock")
    private Integer stock = 0; // 추가 가격 (옵션 선택 시 가격 증가분) , null오류를 위해 기본값 설정해두기

    public GetOption1Dto toGetOption1Dto() {
        if(optionName.equals("옵션없음")){
            return GetOption1Dto.builder()
                    .optionId(id)
                    .optionName("옵션없음")
                    .optionStock(stock)
                    .optionValue("옵션없음")
                    .type("noOption")
                    .build();
        } else if(optionName2==null){
            return GetOption1Dto.builder()
                    .optionId(id)
                    .optionPrice(additionalPrice)
                    .optionName(optionName)
                    .optionValue(optionValue)
                    .optionStock(stock)
                    .type("endOption")
                    .build();
        } else {
            return GetOption1Dto.builder()
                    .optionId(id)
                    .optionName(optionName)
                    .optionValue(optionValue)
                    .type("nextOption")
                    .build();
        }

    }
    public GetOption1Dto toGetOption2Dto() {
        if(optionName3==null){
            return GetOption1Dto.builder()
                    .optionId(id)
                    .optionPrice(additionalPrice)
                    .optionName(optionName2)
                    .optionValue(optionValue2)
                    .optionStock(stock)
                    .type("endOption2")
                    .build();
        } else {
            return GetOption1Dto.builder()
                    .optionId(id)
                    .optionName(optionName2)
                    .optionValue(optionValue2)
                    .type("nextOption2")
                    .build();
        }
    }
}
