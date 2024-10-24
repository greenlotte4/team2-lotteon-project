package com.lotteon.dto.requestDto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCartSaveDto {

    private Long cartItemId;
    private Long productId;
    private int quantity;

}
