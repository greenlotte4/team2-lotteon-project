package com.lotteon.dto.responseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class GetAdminOrderNameDto {
    private Long orderItemId;
    private String orderItemName;
}
