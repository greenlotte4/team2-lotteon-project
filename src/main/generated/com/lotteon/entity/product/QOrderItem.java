package com.lotteon.entity.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderItem is a Querydsl query type for OrderItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderItem extends EntityPathBase<OrderItem> {

    private static final long serialVersionUID = -1258177263L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderItem orderItem = new QOrderItem("orderItem");

    public final NumberPath<Integer> deli = createNumber("deli", Integer.class);

    public final StringPath deliCompany = createString("deliCompany");

    public final NumberPath<Integer> discount = createNumber("discount", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QOrder order;

    public final QProduct product;

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final StringPath req = createString("req");

    public final ListPath<OrderItemOption, QOrderItemOption> selectedOptions = this.<OrderItemOption, QOrderItemOption>createList("selectedOptions", OrderItemOption.class, QOrderItemOption.class, PathInits.DIRECT2);

    public final com.lotteon.entity.member.QSeller seller;

    public final BooleanPath state1 = createBoolean("state1");

    public final NumberPath<Integer> state2 = createNumber("state2", Integer.class);

    public final NumberPath<Integer> total = createNumber("total", Integer.class);

    public final DateTimePath<java.sql.Timestamp> warranty = createDateTime("warranty", java.sql.Timestamp.class);

    public QOrderItem(String variable) {
        this(OrderItem.class, forVariable(variable), INITS);
    }

    public QOrderItem(Path<? extends OrderItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderItem(PathMetadata metadata, PathInits inits) {
        this(OrderItem.class, metadata, inits);
    }

    public QOrderItem(Class<? extends OrderItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new QOrder(forProperty("order"), inits.get("order")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product")) : null;
        this.seller = inits.isInitialized("seller") ? new com.lotteon.entity.member.QSeller(forProperty("seller"), inits.get("seller")) : null;
    }

}

