package com.lotteon.entity.product;

import com.lotteon.entity.member.Customer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id")
    private Customer customer;

    @Column(name = "order_payment")
    private int orderPayment;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_addr")
    private String receiverAddr;

    @Column(name = "receiver_hp")
    private String receiverHp;

    @Column(name = "order_total")
    private int orderTotal;

    @Column(name = "order_deli")
    private int orderDeli;

    @Column(name = "order_quantity")
    private int orderQuantity;

    @Column(name = "order_state")
    private Boolean orderState;

    @Column(name = "order_discount")
    private int orderDiscount;

    @Column(name = "order_rdate")
    @CreationTimestamp
    private Timestamp orderRdate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<OrderItem> orderItems = new ArrayList<>();


}
