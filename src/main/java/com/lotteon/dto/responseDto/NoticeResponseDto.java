package com.lotteon.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDto {

    private Long id;  // 공지사항 ID

    private String memberName;  // 작성자 이름 (Member의 이름)

    private String category1Name;  // 카테고리 1 이름

    private String category2Name;  // 카테고리 2 이름 (optional)

    private String noticeTitle;  // 공지사항 제목

    private Timestamp noticeRdate;  // 등록 날짜

    private String noticeContent;  // 공지사항 내용

    private int noticeViews;  // 조회수
}
