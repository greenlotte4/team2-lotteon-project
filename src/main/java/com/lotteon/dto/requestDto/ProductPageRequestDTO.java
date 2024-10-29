package com.lotteon.dto.requestDto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPageRequestDTO {

    @Builder.Default
    private String sort = null;

    @Builder.Default
    private int no = 1;

    @Builder.Default
    private int pg = 1;

    @Builder.Default
    private int size = 10;

    private String type;
    private String keyword;

    public Pageable getPageable(String sort) {
        return PageRequest.of(this.pg -1, this.size, Sort.by(sort).descending());
    }

}
