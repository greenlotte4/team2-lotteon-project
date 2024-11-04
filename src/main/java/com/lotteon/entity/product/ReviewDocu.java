package com.lotteon.entity.product;

import com.lotteon.dto.responseDto.GetReviewsDto;
import com.lotteon.entity.member.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Document(collection = "review")
public class ReviewDocu {
    @Id
    private String id;

    @JoinColumn(name = "prod_id")
    private Long prodId;

    @JoinColumn(name = "prod_name")
    private String prodName;

    @JoinColumn(name = "cust_id")
    private Long custId;

    @Column(name = "review_score")
    private Integer reviewScore;

    @Column(name = "review_content")
    private String reviewContent;

    @Column(name = "review_rdate")
    @CreationTimestamp
    private LocalDateTime reviewRdate;

    public GetReviewsDto toGetReviewsDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String rdate = reviewRdate.format(formatter);
        return GetReviewsDto.builder()
                .review(reviewContent)
                .prodId(prodId)
                .score(reviewScore)
                .prodName(prodName)
                .rdate(rdate)
                .build();
    }
}