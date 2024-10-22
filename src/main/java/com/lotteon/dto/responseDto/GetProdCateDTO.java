package com.lotteon.dto.responseDto;

import com.lotteon.entity.category.CategoryProduct;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GetProdCateDTO {

    private Long categoryId;
    private String categoryName;
    private int categoryLevel;
    private CategoryProduct parent;
    private Integer categoryOrder;

    @Override
    public String toString() {
        return "categoryId=" + categoryId +
                ", categoryName=" + categoryName +
                ", categoryLevel=" + categoryLevel +
                ", parent=" + parent +
                ", categoryOrder=" + categoryOrder;
    }

}
