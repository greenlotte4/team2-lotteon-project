package com.lotteon.dto.requestDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class GetProductNamesDto {
    private String productName;
    private Long prodId;
}
