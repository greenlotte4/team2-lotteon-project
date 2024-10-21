package com.lotteon.dto.requestDto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostCustSignupDTO { // post customer 일반회원 회원가입

    private String  memId;          // 아이디
    private String  memPwd;         // 비밀번호
    private String  custName;       // 이름
    private Boolean custGender;     // 성별
    private String  custEmail;      // 이메일
    private String  custHp;         // 휴대폰 번호
    private String  addr1;          // 주소1
    private String  addr2;          // 주소2
    private String  addr3;          // 주소3
    private Boolean basicAddr;      // 배송주소 설정 여부
    private Boolean custOptional;    // 선택약관 동의 여부
    private String  memRole;        // 사용자 유형 (admin, seller, buyer)
    private String  custBirth;      // 생일

    private Integer custPoint;      // 포인트 (회원가입 축하포인트 default = "0")

}
