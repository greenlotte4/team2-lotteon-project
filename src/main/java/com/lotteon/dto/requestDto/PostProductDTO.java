package com.lotteon.dto.requestDto;

import com.lotteon.entity.category.CategoryProductMapper;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.product.Product;
import com.lotteon.entity.product.ProductOption;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostProductDTO {

    private Long id;
    private Long sellId;
    private List<CategoryProductMapper> categoryMappings = new ArrayList<>();
    private String prodSummary;
    private Double prodPrice;
    private Double prodDiscount;
    private int prodDeliver;
    private String prodName;
    private String prodListImg;
    private String prodBasicImg;
    private String prodDetailImg;
    private String descriptionName;
    private int prodStock;
    private int prodOrderCnt;
    private int prodViews;
    private int prodRating;
    private int prodPoint;
    @CreationTimestamp
    private Timestamp prodRdate;
    private List<ProductOption> options = new ArrayList<>();

    private MultipartFile listImage;
    private MultipartFile basicImage;
    private MultipartFile detailImage;
    private MultipartFile description;

    private String sellCompany;
    private Integer sellGrade;
    private double totalPrice;

    private Seller seller;

    public void updateSeller(Product product, long prodId) {
        this.id = prodId;
        this.seller = product.getSeller();
        this.sellId = product.getSeller().getId();
        this.sellGrade = product.getSeller().getSellGrade();
        this.sellCompany = product.getSeller().getSellCompany();
    }

}
