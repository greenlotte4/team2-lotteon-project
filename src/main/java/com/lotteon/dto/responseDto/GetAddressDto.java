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
    private String addrNick;
    private String name;
    private String hp1;
    private String hp2;
    private String hp3;
    private String addr1;
    private String addr2;
    private String addr3;
    private String request;
    private int state;
    private String payment;
}
