package com.lotteon.dto.responseDto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAddressDto {
    private Long id;
    private String addr1;
    private String addr2;
    private String addr3;
    private String payment;
    private int state;
    private String addrNick;
}
