package com.lotteon.entity.category;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategoryProduct is a Querydsl query type for CategoryProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategoryProduct extends EntityPathBase<CategoryProduct> {

    private static final long serialVersionUID = -1028567502L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategoryProduct categoryProduct = new QCategoryProduct("categoryProduct");

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    public final NumberPath<Integer> categoryLevel = createNumber("categoryLevel", Integer.class);

    public final StringPath categoryName = createString("categoryName");

    public final NumberPath<Integer> categoryOrder = createNumber("categoryOrder", Integer.class);

    public final ListPath<CategoryProduct, QCategoryProduct> children = this.<CategoryProduct, QCategoryProduct>createList("children", CategoryProduct.class, QCategoryProduct.class, PathInits.DIRECT2);

    public final QCategoryProduct parent;

    public final ListPath<CategoryProductMapper, QCategoryProductMapper> productMappings = this.<CategoryProductMapper, QCategoryProductMapper>createList("productMappings", CategoryProductMapper.class, QCategoryProductMapper.class, PathInits.DIRECT2);

    public QCategoryProduct(String variable) {
        this(CategoryProduct.class, forVariable(variable), INITS);
    }

    public QCategoryProduct(Path<? extends CategoryProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategoryProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategoryProduct(PathMetadata metadata, PathInits inits) {
        this(CategoryProduct.class, metadata, inits);
    }

    public QCategoryProduct(Class<? extends CategoryProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parent = inits.isInitialized("parent") ? new QCategoryProduct(forProperty("parent"), inits.get("parent")) : null;
    }

}

