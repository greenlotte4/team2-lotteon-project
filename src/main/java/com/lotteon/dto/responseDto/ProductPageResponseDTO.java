package com.lotteon.dto.responseDto;

import com.lotteon.dto.requestDto.ProductPageRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponseDTO<T> {
    private List<T> dtoList;
    private int pg;
    private int size;
    private int total;
    private int startNo;
    private int start, end;
    private boolean prev,next;

    private String type;
    private String keyword;

    @Builder
    public ProductPageResponseDTO(ProductPageRequestDTO productPageRequestDTO, List<T> dtoList, int total) {
        this.pg = productPageRequestDTO.getPg();
        this.size = productPageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        this.type = productPageRequestDTO.getType();
        this.keyword = productPageRequestDTO.getKeyword();

        this.startNo = total - ((pg - 1) * size);
        this.end = (int) (Math.ceil((double) this.pg / (double)size)) * size;
        this.start = this.end - (size-1);

        int last = (int) (Math.ceil(total / (double)size));
        this.end = Math.min(end, last);
        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }
}
