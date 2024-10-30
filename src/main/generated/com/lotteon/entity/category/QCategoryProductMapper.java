package com.lotteon.entity.category;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategoryProductMapper is a Querydsl query type for CategoryProductMapper
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategoryProductMapper extends EntityPathBase<CategoryProductMapper> {

    private static final long serialVersionUID = 1038551827L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategoryProductMapper categoryProductMapper = new QCategoryProductMapper("categoryProductMapper");

    public final QCategoryProduct category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.lotteon.entity.product.QProduct product;

    public QCategoryProductMapper(String variable) {
        this(CategoryProductMapper.class, forVariable(variable), INITS);
    }

    public QCategoryProductMapper(Path<? extends CategoryProductMapper> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategoryProductMapper(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategoryProductMapper(PathMetadata metadata, PathInits inits) {
        this(CategoryProductMapper.class, metadata, inits);
    }

    public QCategoryProductMapper(Class<? extends CategoryProductMapper> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategoryProduct(forProperty("category"), inits.get("category")) : null;
        this.product = inits.isInitialized("product") ? new com.lotteon.entity.product.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

