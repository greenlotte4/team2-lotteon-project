package com.lotteon.entity.member;

import com.lotteon.dto.responseDto.GetAdminUserDTO;
import com.lotteon.entity.point.Point;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Default;

import java.time.LocalDate;
import java.util.List;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // customer-member: 일대일 참조 (LAZY.실제로 필요할 때까지 로드되지 않음)
    @JoinColumn(name = "mem_id")
    @ToString.Exclude
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,mappedBy = "customer",orphanRemoval = true)
    @ToString.Exclude
    private List<Point> points;

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



    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    @ToString.Exclude
    private AttendanceEvent attendanceEvent;

    public void updatePoint(int point){
        this.custPoint = point;
    }

    // 회원 정보 수정 (팝업호출) (Member Entity에는 회원 수정할 내용이 없다!)
    // 이름, 성별, 이메일, 휴대폰, 우편+기본+상세주소, 기타
    public void updateUser(GetAdminUserDTO dto){
        this.custName = dto.getCustName();
        this.custGender = dto.getCustGender();
        this.custEmail = dto.getCustEmail();
        this.custHp = dto.getCustHp();
        this.custAddr = dto.getCustAddr1() + "/" + dto.getCustAddr2() + "/" + dto.getCustAddr3();
    }

    public void setGrade(String grade){
        this.custGrade = grade;
    }


}


