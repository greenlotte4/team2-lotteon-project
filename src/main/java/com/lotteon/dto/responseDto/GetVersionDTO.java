package com.lotteon.dto.responseDto;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetVersionDTO {
    private Long id;

    private Long mem_id;

    private String verName;

    private String verContent;

    private Timestamp verRdate;

    private String memUid;

}
