package com.lotteon.dto.requestDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class PostFindIdDto {

    private String name;

    private String email;

    private String uid;

    private String pwd;
}
