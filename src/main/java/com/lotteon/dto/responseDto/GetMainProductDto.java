package com.lotteon.dto.responseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class GetMainProductDto {
    private String img;
    private String prodName;
    private Double prodPrice; 
    private Double prodDiscount;
    private Long id;
    private String type;
    private String deli;
}
