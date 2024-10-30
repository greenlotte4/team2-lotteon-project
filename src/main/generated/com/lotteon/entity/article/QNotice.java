package com.lotteon.entity.article;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotice is a Querydsl query type for Notice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotice extends EntityPathBase<Notice> {

    private static final long serialVersionUID = -420045471L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotice notice = new QNotice("notice");

    public final com.lotteon.entity.category.QCategoryArticle cate1;

    public final com.lotteon.entity.category.QCategoryArticle cate2;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.lotteon.entity.member.QMember member;

    public final StringPath noticeContent = createString("noticeContent");

    public final DateTimePath<java.sql.Timestamp> noticeRdate = createDateTime("noticeRdate", java.sql.Timestamp.class);

    public final StringPath noticeTitle = createString("noticeTitle");

    public final NumberPath<Integer> noticeViews = createNumber("noticeViews", Integer.class);

    public QNotice(String variable) {
        this(Notice.class, forVariable(variable), INITS);
    }

    public QNotice(Path<? extends Notice> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotice(PathMetadata metadata, PathInits inits) {
        this(Notice.class, metadata, inits);
    }

    public QNotice(Class<? extends Notice> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cate1 = inits.isInitialized("cate1") ? new com.lotteon.entity.category.QCategoryArticle(forProperty("cate1"), inits.get("cate1")) : null;
        this.cate2 = inits.isInitialized("cate2") ? new com.lotteon.entity.category.QCategoryArticle(forProperty("cate2"), inits.get("cate2")) : null;
        this.member = inits.isInitialized("member") ? new com.lotteon.entity.member.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

