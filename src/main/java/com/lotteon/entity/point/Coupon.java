package com.lotteon.entity.point;

import com.lotteon.dto.responseDto.GetCouponDto;
import com.lotteon.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "coupon_name")
    private String couponName;
    @Column(name = "coupon_discount_option")
    private String couponDiscountOption;

    @Column(name = "coupon_discount")
    private Integer couponDiscount;

    @Column(name = "coupon_type")
    private String couponType;

    @Column(name = "coupon_expiration")
    private String couponExpiration;

    @Column(name = "coupon_min_price")

    private Integer couponMinPrice;


    @Column(name = "coupon_caution", columnDefinition = "TEXT")
    private String couponCaution;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_issuer")
    private Member member;

    @Column(name = "coupon_issue_count")
    private Integer couponIssueCount;

    @Column(name = "coupon_use_count")
    private Integer couponUseCount;

    @Column(name = "coupon_state")
    private String couponState;

    @Column(name = "coupon_rdate")
    @CreationTimestamp
    private Timestamp couponRdate;

    @Column(name = "customer_coupont_expiration")
    private Integer customerCouponExpiration;

    @Column(name = "coupon_banner_state")
    private Integer couponBannerState;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<CustomerCoupon> customerCoupons = new ArrayList<>();

    public GetCouponDto toGetCouponDto() {

        String realOption1 ="상품 ";
        String realOption2 ="원 할인 쿠폰";
        if(couponDiscountOption.equals("p")) {
            realOption2 = "% 할인 쿠폰";
        } else if (couponDiscountOption.equals("d")){
            realOption1 = "배송비 ";
        }
        String issuer ;
        if(member.getMemRole().equals("admin")){
            issuer = "운영자";
        } else {
            issuer = member.getSeller().getSellCompany();
        }
        String banner;
        if(couponBannerState==0){
            banner = "이벤트";
        } else {
            banner = "이벤트종료";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(couponRdate);

        return GetCouponDto.builder()
                .couponExpiration(couponExpiration)
                .couponIssueCount(couponIssueCount)
                .couponIssuer(issuer)
                .couponRdate(formattedDate)
                .couponState(couponState)
                .id(id)
                .couponType(couponType)
                .couponDiscount(couponDiscount)
                .couponDiscountOption1(realOption1)
                .couponDiscountOption2(realOption2)
                .couponName(couponName)
                .couponUseCount(couponUseCount)
                .couponCaution(couponCaution)
                .couponBannerState(banner)
                .couponMinPrice(couponMinPrice)
                .build();
    }

    public void updateCouponState(){
        if(couponState.equals("종료")){
            couponState = "발급중";
        } else {
            couponState = "종료";
        }
    }


    public void updateCouponBannerState(int a) {
        this.couponBannerState = a;
    }

    public void updateCouponUseCnt() {
        this.couponUseCount = couponUseCount + 1;
    }

    public void updateCouponIssueCnt() {
        this.couponIssueCount = couponIssueCount + 1;
    }
}
