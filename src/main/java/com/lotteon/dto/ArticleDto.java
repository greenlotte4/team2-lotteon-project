package com.lotteon.dto;

import lombok.*;

import java.security.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ArticleDto {
    private Long id;
    private String title;        // 공통: 제목 (FAQ, Notice, QnA 모두에서 사용)
    private String content;      // 공통: 내용 (FAQ, Notice, QnA 모두에서 사용)
    private String answer;       // QnA 전용: 답변 내용
    private LocalDateTime rdate;
    private int views;
    private int state;           // QnA 전용: 답변 상태 (답변 완료/대기 등)
    private int type;            // QnA 전용: 문의 대상 (판매자 or 관리자)
    private Long cate1Id;
    private Long cate2Id;
    private Long memId;

    private String adminName;    // 관리자 이름 (관리자용 추가 필드)
    private String adminEmail;   // 관리자 이메일 (관리자용 추가 필드)

    private LocalDateTime updateDate;  // 업데이트된 날짜 (필요 시 추가)
    private String status;       // 글 상태 (예: '작성 중', '게시 완료' 등)

}
