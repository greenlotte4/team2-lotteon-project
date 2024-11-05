package com.lotteon.entity.product;

import com.lotteon.dto.responseDto.GetAdminOrderNameDto;
import com.lotteon.entity.member.Seller;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id")
    private Product product;

    @Column(name = "order_item_quantity")
    private int quantity;

    @Column(name = "order_item_total")
    private int total;

    @Column(name = "order_item_state1")
    @ColumnDefault("0")
    private int state1;

    @Column(name = "order_item_state2")
    @ColumnDefault("0")
    @Setter
    private int state2;

    @Column(name = "order_item_discount")
    private int discount;

    @Column(name = "order_item_deli")
    private int deli;

    @Column(name = "order_item_warranty")
    private Timestamp warranty;

    @Column(name = "order_deli_sdate")
    private LocalDate deliSdate;

//    @OneToMany(mappedBy = "orderItem")
//    private List<OrderItemOption> selectedOptions;

    //추가 필드
    @Setter
    @Column(name = "order_deli_id")
    private String orderDeliId;

    @Setter
    @Column(name = "order_deli_company")
    private Integer orderDeliCompany;

    @Column(name = "order_item_option_id")
    private Long optionId;

    public void updateState2(int i) {
        this.state2 = i;
    }

    public GetAdminOrderNameDto toGetAdminOrderNameDto() {
        return GetAdminOrderNameDto.builder()
                .orderItemId(id)
                .orderItemName(product.getProdName())
                .build();
    }

    public void setOrderDeliSdate(LocalDate today) {
        this.deliSdate = today;
    }

}
