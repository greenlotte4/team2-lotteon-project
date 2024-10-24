package com.lotteon.service.product;

import com.lotteon.dto.requestDto.PostCartSaveDto;
import com.lotteon.dto.responseDto.GetOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    public List<GetOrderDto> selectedOrders(List<PostCartSaveDto> selectedProducts) {
        return null;
    }
}
