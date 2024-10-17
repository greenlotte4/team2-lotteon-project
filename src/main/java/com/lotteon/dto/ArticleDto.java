package com.lotteon.dto;

import lombok.*;

import java.security.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ArticleDto {
    private Long id;             // 공통: 게시물 ID
    private String title;        // 공통: 제목 (FAQ, Notice, QnA 모두에서 사용)
    private String content;      // 공통: 내용 (FAQ, Notice, QnA 모두에서 사용)
    private String answer;       // QnA 전용: 답변 내용
    private Timestamp rdate;     // 공통: 작성일자
    private int views;           // 공통: 조회수
    private int state;           // QnA 전용: 답변 상태 (답변 완료/대기 등)
    private int type;            // QnA 전용: 문의 대상 (판매자 or 관리자)
    private Long cate1Id;        // 공통: 1차 카테고리 ID
    private Long cate2Id;        // 공통: 2차 카테고리 ID
    private String memberName;   // 공통: 작성자 이름
}
