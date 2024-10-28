package com.lotteon.dto.responseDto;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/*
     날짜 : 2024/10/23 (수)
     이름 : 김민희
     내용 : 관리자 회원목록 DTO 생성

     수정이력
      - 2025/10/26 (토) | 김민희 : 회원 수정에 필요한 필드 추가
*/

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class GetAdminUserDTO {
    // 번호, 아이디, 이름, 성별, 등급, 포인트, 이메일, 휴대폰, 가입일, 상태, 관리
    // 관리자 회원목록
    private Long id;                // 번호
    private String  memUid;         // 아이디
    private String  custName;       // 이름

    private String custGrade;       // 사용자 등급 (4가지 - VVIP, VIP, GOLD, SILVER, FAMILY)

    private Boolean custGender;     // 성별
    private Integer custPoint;      // 포인트 (회원가입 축하포인트 default = "0")

    private String  custEmail;      // 이메일
    private String  custHp;         // 휴대폰 번호

    @CreationTimestamp
    private Timestamp memRdate;     // 가입일
    private String memState;        // 계정 상태 (활성, 비활성) [로그인, 로그아웃]


    // 회원 수정 (팝업에 필요한 추가 필드)
    // 아이디(x), 이름(-), 성별(-), 등급(-), 상태(x), 등급(x), 이메일(-), 휴대폰(-),
    // 우편번호, 기본주소, 상세주소, 가입일, 최근 로그인 날짜(x), 기타
    private String custAddr1; // 우편
    private String custAddr2; // 주소
    private String custAddr3; // 상세주소




}

