package com.lotteon.dto.responseDto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class GetCouponDto {
    private Long id;
    private String couponType;
    private String couponName;
    private int  couponDiscount;
    private String couponDiscountOption1;
    private String couponDiscountOption2;
    private String couponExpiration;
    private String couponIssuer;
    private int couponIssueCount;
    private int couponUseCount;
    private String couponState;
    private String couponRdate;
    private String couponCaution;
    private String couponBannerState;
    private int couponMinPrice;
}
