package com.lotteon.dto.responseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class GetReviewsDto {
    private String prodName;
    private Long prodId;
    private String review;
    private Integer score;
    private String rdate;
    private String img;
    private String memUid;
    private String reviewImg;
}
