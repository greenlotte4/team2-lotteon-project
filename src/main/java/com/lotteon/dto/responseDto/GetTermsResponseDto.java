package com.lotteon.dto.responseDto;

import com.lotteon.entity.term.Terms;
import jakarta.persistence.Column;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTermsResponseDto {

    private Long id;
    private String termsName;
    private String termsContent;
    private String termsType;

    private String termsTitle;

    public Terms toEntity() {
        return Terms.builder()
                .id(id)
                .termsName(termsName)
                .termsContent(termsContent)
                .termsType(termsType)
                .build();
    }
}
