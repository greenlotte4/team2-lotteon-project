package com.lotteon.entity.category;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategoryArticle is a Querydsl query type for CategoryArticle
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategoryArticle extends EntityPathBase<CategoryArticle> {

    private static final long serialVersionUID = -1451471303L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategoryArticle categoryArticle = new QCategoryArticle("categoryArticle");

    public final StringPath categoryIcon = createString("categoryIcon");

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    public final NumberPath<Integer> categoryLevel = createNumber("categoryLevel", Integer.class);

    public final StringPath categoryName = createString("categoryName");

    public final NumberPath<Integer> categoryType = createNumber("categoryType", Integer.class);

    public final StringPath categoryWarning = createString("categoryWarning");

    public final ListPath<CategoryArticle, QCategoryArticle> children = this.<CategoryArticle, QCategoryArticle>createList("children", CategoryArticle.class, QCategoryArticle.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> noticeDate = createDateTime("noticeDate", java.time.LocalDateTime.class);

    public final QCategoryArticle parent;

    public QCategoryArticle(String variable) {
        this(CategoryArticle.class, forVariable(variable), INITS);
    }

    public QCategoryArticle(Path<? extends CategoryArticle> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategoryArticle(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategoryArticle(PathMetadata metadata, PathInits inits) {
        this(CategoryArticle.class, metadata, inits);
    }

    public QCategoryArticle(Class<? extends CategoryArticle> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parent = inits.isInitialized("parent") ? new QCategoryArticle(forProperty("parent"), inits.get("parent")) : null;
    }

}

