package com.lotteon.entity.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductDetail is a Querydsl query type for ProductDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductDetail extends EntityPathBase<ProductDetail> {

    private static final long serialVersionUID = 637106096L;

    public static final QProductDetail productDetail = new QProductDetail("productDetail");

    public final StringPath cardEvent = createString("cardEvent");

    public final StringPath cardType = createString("cardType");

    public final StringPath caution = createString("caution");

    public final BooleanPath deliable = createBoolean("deliable");

    public final NumberPath<Integer> deliDate = createNumber("deliDate", Integer.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath installmentable = createBoolean("installmentable");

    public final StringPath madein = createString("madein");

    public final StringPath manufacture = createString("manufacture");

    public final DateTimePath<java.sql.Timestamp> mdate = createDateTime("mdate", java.sql.Timestamp.class);

    public final StringPath origin = createString("origin");

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final StringPath stat = createString("stat");

    public final BooleanPath tax = createBoolean("tax");

    public final NumberPath<Integer> warranty = createNumber("warranty", Integer.class);

    public final BooleanPath warrantyType = createBoolean("warrantyType");

    public QProductDetail(String variable) {
        super(ProductDetail.class, forVariable(variable));
    }

    public QProductDetail(Path<? extends ProductDetail> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductDetail(PathMetadata metadata) {
        super(ProductDetail.class, metadata);
    }

}

