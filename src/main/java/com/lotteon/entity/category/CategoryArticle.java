package com.lotteon.entity.category;

import com.lotteon.dto.responseDto.GetArticleCategoryDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category_article")
public class CategoryArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_level")
    private int categoryLevel;

    @Column(name = "category_icon")
    private String categoryIcon;

    @Column(name = "category_warning" ,columnDefinition = "TEXT")
    private String categoryWarning;

    @Column(name = "category_type")
    private int categoryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private CategoryArticle parent;

    @ToString.Exclude
    @OneToMany(mappedBy = "parent")
    private List<CategoryArticle> children;

    // 공지사항 날짜 필드 추가
    @Column(name = "notice_date")
    private LocalDateTime noticeDate;

    public GetArticleCategoryDto toGetArticleCategoryDto() {
        return GetArticleCategoryDto.builder()
                .id(categoryId)
                .categoryName(categoryName)
                .build();
    }
}
