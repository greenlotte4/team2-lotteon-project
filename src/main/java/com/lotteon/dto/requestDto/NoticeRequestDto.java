package com.lotteon.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDto {

    private Long id;  // 공지사항 ID (수정 시 필요)

    private Long memberId;  // 작성자 ID (작성자 식별자)

    private Long cate1Id;  // 카테고리 1 ID

    private Long cate2Id;  // 카테고리 2 ID (optional)

    private String noticeTitle;  // 공지사항 제목

    private String noticeContent;  // 공지사항 내용
}
