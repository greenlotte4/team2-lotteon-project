package com.lotteon.entity.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = 1958505694L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final com.lotteon.entity.member.QCustomer customer;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> orderDeli = createNumber("orderDeli", Integer.class);

    public final NumberPath<Integer> orderDiscount = createNumber("orderDiscount", Integer.class);

    public final ListPath<OrderItem, QOrderItem> orderItems = this.<OrderItem, QOrderItem>createList("orderItems", OrderItem.class, QOrderItem.class, PathInits.DIRECT2);

    public final NumberPath<Integer> orderPayment = createNumber("orderPayment", Integer.class);

    public final NumberPath<Integer> orderQuantity = createNumber("orderQuantity", Integer.class);

    public final DateTimePath<java.sql.Timestamp> orderRdate = createDateTime("orderRdate", java.sql.Timestamp.class);

    public final StringPath orderReq = createString("orderReq");

    public final NumberPath<Integer> orderState = createNumber("orderState", Integer.class);

    public final NumberPath<Integer> orderTotal = createNumber("orderTotal", Integer.class);

    public final StringPath receiverAddr = createString("receiverAddr");

    public final StringPath receiverHp = createString("receiverHp");

    public final StringPath receiverName = createString("receiverName");

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new com.lotteon.entity.member.QCustomer(forProperty("customer"), inits.get("customer")) : null;
    }

}

