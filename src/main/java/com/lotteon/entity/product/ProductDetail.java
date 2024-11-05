package com.lotteon.entity.product;

import com.lotteon.dto.requestDto.PostProdDetailDTO;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_detail")
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prod_manufacture")
    private String manufacture;

    @Column(name = "prod_id")
    private Long productId;

    @Column(name = "prod_madein")
    private String madein;

    @Column(name = "prod_caution")
    private String caution;

    @Column(name = "prod_mdate")
    private Timestamp mdate;

    @Column(name = "prod_warranty")
    private int warranty;

    @Column(name = "prod_warranty_type")
    private Boolean warrantyType;

    @Column(name = "prod_description")
    private String description;

    @Column(name = "prod_stat")
    private String stat;

    @Column(name = "prod_tax")
    private Boolean tax;

    @Column(name = "prod_origin")
    private String origin;

    @Column(name = "prod_deli_able")
    private Boolean deliable;

    @Column(name = "prod_installment_able")
    private Boolean installmentable;

    @Column(name = "prod_card_event")
    private String cardEvent;

    @Column(name = "prod_card_type")
    private String cardType;

    @Column(name = "prod_deli_date")
    private int deliDate;

    public void updateDetail(PostProdDetailDTO dto) {
        this.manufacture = dto.getManufacture();
        this.productId = dto.getProductId();
        this.madein = dto.getMadein();
        this.caution = dto.getCaution();
        this.mdate = dto.getMdate();
        this.warranty = dto.getWarranty();
        this.warrantyType = dto.getWarrantyType();
        this.description = dto.getDescription();
        this.stat = dto.getStat();
        this.tax = dto.getTax();
        this.origin = dto.getOrigin();
        this.id = dto.getId();
        this.cardEvent = dto.getCardEvent();
        this.cardType = dto.getCardType();
    }
}
