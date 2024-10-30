package com.lotteon.dto.requestDto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostProdDetailDTO {

    private Long id;
    private String manufacture;
    private Long productId;
    private String madein;
    private String caution;
    private int warranty;
    private Boolean warrantyType;
    private String description;
    private String stat;
    private Boolean tax;
    private String origin;
    private Boolean deliable;
    private Boolean installmentable;
    private String cardEvent;
    private String cardType;
    private int deliDate;
    @CreationTimestamp
    private Timestamp mdate;

    private LocalDate mDate1;
}
