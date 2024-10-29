package com.lotteon.dto.responseDto.cartOrder;

import com.lotteon.dto.requestDto.cartOrder.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/*
    날짜: 2024/10/27
    이름: 박연화
    내용: responseOrderDto 생성

    수정이력
    - 2024/10/28 박연화 - admin orders에 출력될 memUid 추가
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderDto {
    private Long orderId;
    private String custName;
    private String custHp;
    private String receiverName;
    private String receiverHp;
    private String receiverAddr;
    private int OrderTotal;
    private List<ResponseOrderItemDto> orderItemDtos;

}
