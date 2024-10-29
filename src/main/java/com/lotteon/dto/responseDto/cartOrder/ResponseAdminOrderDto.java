package com.lotteon.dto.responseDto.cartOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAdminOrderDto {
    private Long orderId;
    private String memUid;
    private String custName;
    private String ProdName;
    private int OrderItemCount;
    private int OrderItemTotal;
    private int OrderState;
    private String OrderRdate;
}
