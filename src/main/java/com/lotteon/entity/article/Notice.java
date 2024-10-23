package com.lotteon.entity.article;

import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_id", nullable = false)
    private Member member;  // 작성자 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_cate1", nullable = false)
    private CategoryArticle cate1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_cate2")
    private CategoryArticle cate2;  // 카테고리 2 (optional)

    @Column(name = "notice_title", nullable = false)
    private String noticeTitle;  // 공지사항 제목

    @CreationTimestamp
    @Column(name = "notice_rdate", updatable = false)
    private Timestamp noticeRdate;  // 등록 날짜

    @Column(name = "notice_content", columnDefinition = "TEXT", nullable = false)
    private String noticeContent;  // 공지사항 내용

    @Column(name = "notice_views", nullable = false)
    private int noticeViews = 0;  // 조회수, 기본값 0

    // 공지사항 제목 수정
    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    // 공지사항 내용 수정
    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    // 조회수 증가 메서드
    public void incrementViews() {
        this.noticeViews++;
    }

    // 작성자(Member) 설정
    public void setMember(Member member) {
        this.member = member;
    }

    // 카테고리 1 설정
    public void setCate1(CategoryArticle cate1) {
        this.cate1 = cate1;
    }

    // 카테고리 2 설정 (optional)
    public void setCate2(CategoryArticle cate2) {
        this.cate2 = cate2;
    }
}