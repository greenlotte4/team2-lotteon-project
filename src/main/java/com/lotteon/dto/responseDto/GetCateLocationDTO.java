package com.lotteon.dto.responseDto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCateLocationDTO {

    private String level1Name;
    private String level2Name;
    private String level3Name;

}
