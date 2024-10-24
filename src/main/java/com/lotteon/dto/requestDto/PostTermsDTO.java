package com.lotteon.dto.requestDto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostTermsDTO {
    private Long id;
    private String termsContent;
}
