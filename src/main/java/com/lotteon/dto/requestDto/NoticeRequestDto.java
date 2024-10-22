package com.lotteon.dto.requestDto;

import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.entity.member.Member;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestDto {

    private Long id;  // 공지사항 ID (수정 시 필요)

    private Long memberId;  // 작성자 ID (작성자 식별자)

    private CategoryArticle cate1;  // 카테고리 1 ID

    private CategoryArticle cate2;  // 카테고리 2 ID (optional)

    private String noticeTitle;  // 공지사항 제목

    private String noticeContent;  // 공지사항 내용

    private Long cateId1;
    private Long cateId2;

    private Member member;

}
