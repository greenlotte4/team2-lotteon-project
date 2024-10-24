package com.lotteon.entity.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = 1825934975L;

    public static final QProduct product = new QProduct("product");

    public final ListPath<com.lotteon.entity.category.CategoryProductMapper, com.lotteon.entity.category.QCategoryProductMapper> categoryMappings = this.<com.lotteon.entity.category.CategoryProductMapper, com.lotteon.entity.category.QCategoryProductMapper>createList("categoryMappings", com.lotteon.entity.category.CategoryProductMapper.class, com.lotteon.entity.category.QCategoryProductMapper.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<ProductOption, QProductOption> options = this.<ProductOption, QProductOption>createList("options", ProductOption.class, QProductOption.class, PathInits.DIRECT2);

    public final StringPath prodBasicImg = createString("prodBasicImg");

    public final NumberPath<Integer> prodDeliver = createNumber("prodDeliver", Integer.class);

    public final StringPath prodDetailImg = createString("prodDetailImg");

    public final NumberPath<Double> prodDiscount = createNumber("prodDiscount", Double.class);

    public final StringPath prodListImg = createString("prodListImg");

    public final StringPath prodName = createString("prodName");

    public final NumberPath<Integer> prodOrderCnt = createNumber("prodOrderCnt", Integer.class);

    public final NumberPath<Integer> prodPoint = createNumber("prodPoint", Integer.class);

    public final NumberPath<Double> prodPrice = createNumber("prodPrice", Double.class);

    public final NumberPath<Integer> prodRating = createNumber("prodRating", Integer.class);

    public final DateTimePath<java.sql.Timestamp> prodRdate = createDateTime("prodRdate", java.sql.Timestamp.class);

    public final NumberPath<Integer> prodStock = createNumber("prodStock", Integer.class);

    public final StringPath prodSummary = createString("prodSummary");

    public final NumberPath<Integer> prodViews = createNumber("prodViews", Integer.class);

    public final NumberPath<Long> sellId = createNumber("sellId", Long.class);

    public final ListPath<ProductStock, QProductStock> stocks = this.<ProductStock, QProductStock>createList("stocks", ProductStock.class, QProductStock.class, PathInits.DIRECT2);

    public QProduct(String variable) {
        super(Product.class, forVariable(variable));
    }

    public QProduct(Path<? extends Product> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProduct(PathMetadata metadata) {
        super(Product.class, metadata);
    }

}

