package com.lotteon.dto.responseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class GetShopsDto {
    private Long id;
    private String company;
    private String name;
    private String businessCode;
    private String orderCode;
    private String hp;
    private String state;
}
