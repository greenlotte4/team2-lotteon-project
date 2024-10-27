package com.lotteon.entity.article;

import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.entity.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "faq")
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faq_cate1")
    private CategoryArticle cate1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faq_cate2")
    private CategoryArticle cate2;

    @Column(name = "faq_title")
    private String faqTitle;

    @Column(name = "faq_rdate")
    @CreationTimestamp
    private LocalDateTime faqRdate;

    @Column(name = "faq_content", columnDefinition = "TEXT")
    private String faqContent;

    @Column(name = "faq_views")
    private int faqViews;

    public void update(String title, String content, CategoryArticle cate1, CategoryArticle cate2) {
        this.faqTitle = title;
        this.faqContent = content;
        this.cate1 = cate1;
        this.cate2 = cate2;
    }
}
