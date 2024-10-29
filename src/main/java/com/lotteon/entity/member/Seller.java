package com.lotteon.entity.member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seller")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 번호

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mem_id")
    @ToString.Exclude
    private Member member;

    @Column(name = "sell_grade")
    private int sellGrade; // 판매자 등급

    @Column(name = "sell_company")
    private String sellCompany; // 상호명

    @Column(name = "sell_representative")
    private String sellRepresentative; // 대표명

    @Column(name = "sell_business_code")
    private String sellBusinessCode; // 사업자번호

    @Column(name = "sell_order_code")
    private String sellOrderCode; // 통신판매업번호

    @Column(name = "sell_hp")
    private String sellHp; // 판매자 전화번호

    @Column(name = "sell_fax")
    private String sellFax; // 팩스번호

    @Column(name = "sell_email")
    private String sellEmail;

    @Column(name = "sell_addr")
    private String sellAddr; // 판매자 주소
    
    // Entity -> DTO 변환



    
    
}
