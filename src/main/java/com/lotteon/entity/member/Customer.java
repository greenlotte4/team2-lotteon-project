package com.lotteon.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Default;

import java.time.LocalDate;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 번호

    @OneToOne(fetch = FetchType.LAZY) // customer-member: 일대일 참조 (LAZY.실제로 필요할 때까지 로드되지 않음)
    @JoinColumn(name = "mem_id")
    @ToString.Exclude
    private Member member;

    @Column(name = "cust_point")
    private Integer custPoint; // 포인트

    @Column(name = "cust_addr")
    private String custAddr; // 주소

    @Column(name = "cust_name")
    private String custName; // 이름 

    @Column(name = "cust_hp")
    private String custHp; // 휴대폰

    @Column(name = "cust_gender")
    private Boolean custGender; // 성별

    @Column(name = "cust_email")
    private String custEmail; // 이메일

    @Column(name = "cust_birth")
    private String custBirth; // 생일

    @Column(name = "cust_addr_option")
    private Boolean custAddrOption; // 주소 등록 여부 (선택) → 주소 x → 회원가입 가능

    @Column(name = "cust_term_option")
    private Boolean custTermOption; // 선택약관동의여부 (선택:T, 미선택:F)

    @Column(name = "cust_event_checker")
    private int custEventChecker;

    @Column(name = "cust_grade")
    private String custGrade; // 사용자 등급 (VVIP, VIP, Gold, Silver, Family)



    @OneToOne(mappedBy = "customer")
    @ToString.Exclude
    private AttendanceEvent attendanceEvent;

    public void updatePoint(int point){
        this.custPoint = point;
    }
}
