package com.lotteon.entity.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCartItemOption is a Querydsl query type for CartItemOption
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCartItemOption extends EntityPathBase<CartItemOption> {

    private static final long serialVersionUID = -931241704L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCartItemOption cartItemOption = new QCartItemOption("cartItemOption");

    public final QCartItem cartItem;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> prodOptionId = createNumber("prodOptionId", Long.class);

    public QCartItemOption(String variable) {
        this(CartItemOption.class, forVariable(variable), INITS);
    }

    public QCartItemOption(Path<? extends CartItemOption> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCartItemOption(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCartItemOption(PathMetadata metadata, PathInits inits) {
        this(CartItemOption.class, metadata, inits);
    }

    public QCartItemOption(Class<? extends CartItemOption> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cartItem = inits.isInitialized("cartItem") ? new QCartItem(forProperty("cartItem"), inits.get("cartItem")) : null;
    }

}

