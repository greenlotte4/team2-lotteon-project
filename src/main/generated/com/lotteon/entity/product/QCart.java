package com.lotteon.entity.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCart is a Querydsl query type for Cart
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCart extends EntityPathBase<Cart> {

    private static final long serialVersionUID = 755540880L;

    public static final QCart cart = new QCart("cart");

    public final NumberPath<Long> custId = createNumber("custId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<CartItem, QCartItem> items = this.<CartItem, QCartItem>createList("items", CartItem.class, QCartItem.class, PathInits.DIRECT2);

    public QCart(String variable) {
        super(Cart.class, forVariable(variable));
    }

    public QCart(Path<? extends Cart> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCart(PathMetadata metadata) {
        super(Cart.class, metadata);
    }

}

