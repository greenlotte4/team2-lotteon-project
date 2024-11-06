package com.lotteon.entity.member;

import com.lotteon.dto.responseDto.GetAdminUserDTO;
import com.lotteon.entity.point.Coupon;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`member`")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 번호

    @Column(name = "mem_uid")
    private String memUid; // 아이디

    @Column(name = "mem_pwd")
    private String memPwd; // 비밀번호

    @Column(name = "mem_role")
    private String memRole; // 사용자 유형 (admin, seller, buyer)

    @Column(name = "mem_rdate")
    @CreationTimestamp
    private Timestamp memRdate; // 가입일

    @Column(name = "mem_state")
    private String memState; // 계정 상태 (활성, 비활성) [로그인, 로그아웃]

    @Column(name = "mem_last_login_date")
    private LocalDateTime memLastLoginDate; // 최근 로그인 날짜

    @Column(name = "mem_etc")
    private String memEtc; // 기타 (회원 기타 정보입력)

    @Column(name = "mem_edate")
    private Timestamp memSignout; // 탈퇴일자

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Customer customer;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Seller seller;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Coupon> coupons;

    public GetAdminUserDTO toGetAdminUserDTO() {
        // 팝업 호출 (회원 목록 수정)
        if(memRole.equals("customer")){
            return GetAdminUserDTO.builder()
                    .custId(customer.getId()) // 번호
                    .memUid(String.valueOf(customer.getMember().getMemUid())) // 아이디
                    .custName(customer.getCustName()) // 이름
                    .custGender(customer.getCustGender()) // 성별
                    .custGrade(customer.getCustGrade()) // 등급
                    .custPoint(customer.getCustPoint()) // 포인트
                    .custEmail(customer.getCustEmail()) // 이메일
                    .custHp(customer.getCustHp()) // 휴대폰
                    .memRdate(memRdate) // 가입일
                    .memState(String.valueOf(memState)) // 계정 상태 (4가지 - 정산, 중지, 휴면, 탈퇴)
                    .build();
          // 소셜 로그인 사용자일 때 (= guest)
        } else {
            return GetAdminUserDTO.builder()
                    .custId(customer.getId()) // 번호
                    .memUid(String.valueOf(customer.getMember().getMemUid())) // 아이디
                    .custName(customer.getCustName()) // 이름
                    .custGender(customer.getCustGender()) // 성별
                    .custGrade(customer.getCustGrade()) // 등급
                    .custPoint(customer.getCustPoint()) // 포인트
                    .custEmail(customer.getCustEmail()) // 이메일
                    .custHp(customer.getCustHp()) // 휴대폰
                    .memRdate(memRdate) // 가입일
                    .memState(String.valueOf(memState)) // 계정 상태 (4가지 - 정산, 중지, 휴면, 탈퇴)
                    .build();
        }


    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void updateLastLogin(LocalDateTime today) {
        memLastLoginDate = today;
    }

    public void updateMemberStateToSleep() {
        this.memState = "sleep";
    }

    public void updateMemberStateToLeave() {
        this.memState = "leave";
    }

    public void updateMemberState(String state) {this.memState = state;}

    public void updatePassword(String encode) {
        this.memPwd = encode;
    }

    // 관리자 회원수정 (팝업 수정)

    public void updateUser(Customer customer, String memEtc) {
        this.customer = customer;
        this.memEtc = memEtc;
    }
    // 관리자 회원수정 (팝업 수정 후 -> 회원 목록에서 보여짐)
    public void updateListUser(Customer customer, String memEtc) {
        this.customer = customer;
        this.memEtc = memEtc;
    }


    public void updateMemberStateToStart() {
        this.memState = "start";
    }
}
