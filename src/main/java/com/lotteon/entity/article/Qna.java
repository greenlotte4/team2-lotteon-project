package com.lotteon.entity.article;

import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.member.Seller;
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
@Table(name = "qna")
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_cate1")
    private CategoryArticle cate1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_cate2")
    private CategoryArticle cate2;

    @Column(name = "qna_title")
    private String qnaTitle;

    @Column(name = "qna_rdate")
    @CreationTimestamp
    private LocalDateTime qnaRdate;

    @Column(name = "qna_content", columnDefinition = "TEXT")
    private String qnaContent;

    @Column(name = "qna_state")
    private int qnaState;

    @Column(name = "qna_type")
    private int qnaType;

    @Column(name = "qna_answer", columnDefinition = "TEXT")
    private String qnaAnswer;

    @Column(name = "qna_views")
    private int qnaViews;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_id")
    private Seller seller;

    public void changeAnswer(String answer) {
        qnaAnswer = answer;
    }

}
