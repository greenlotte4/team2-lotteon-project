package com.lotteon.dto.responseDto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostAddressDto {
    private String addrNick;
    private String addrName;
    private String addrHp;
    private String addr;
    private String request;
    private Boolean basicState;
    private Long id;
}
