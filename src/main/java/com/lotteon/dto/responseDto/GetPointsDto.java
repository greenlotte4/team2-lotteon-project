package com.lotteon.dto.responseDto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPointsDto {
    private Long id;
    private LocalDate rdate;
    private String pointType;
    private String orderId;
    private int pointVar;
    private String pointEtc;
    private String pointExpiration;
    private String warningExpiration;
}
