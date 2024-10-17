package com.lotteon.dto.requestDto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostCartDto {

    private long id;
    private long custId;
    private long prodId;
    private int quantity;
    private String option1;
    private String option2;
    private String option3;



}

