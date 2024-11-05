package com.lotteon.dto.requestDto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostProdAllDTO {

    private PostProductDTO postProductDTO;
    private PostProdCateMapperDTO postProdCateMapperDTO;
    private PostProdDetailDTO postProdDetailDTO;
    private String type;
    private long prodId;
}
