package com.lotteon.entity.article;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQna is a Querydsl query type for Qna
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQna extends EntityPathBase<Qna> {

    private static final long serialVersionUID = -575249413L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQna qna = new QQna("qna");

    public final com.lotteon.entity.category.QCategoryArticle cate1;

    public final com.lotteon.entity.category.QCategoryArticle cate2;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.lotteon.entity.member.QMember member;

    public final StringPath qnaAnswer = createString("qnaAnswer");

    public final StringPath qnaContent = createString("qnaContent");

    public final DateTimePath<java.time.LocalDateTime> qnaRdate = createDateTime("qnaRdate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> qnaState = createNumber("qnaState", Integer.class);

    public final StringPath qnaTitle = createString("qnaTitle");

    public final NumberPath<Integer> qnaType = createNumber("qnaType", Integer.class);

    public final NumberPath<Integer> qnaViews = createNumber("qnaViews", Integer.class);

    public final com.lotteon.entity.member.QSeller seller;

    public QQna(String variable) {
        this(Qna.class, forVariable(variable), INITS);
    }

    public QQna(Path<? extends Qna> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQna(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQna(PathMetadata metadata, PathInits inits) {
        this(Qna.class, metadata, inits);
    }

    public QQna(Class<? extends Qna> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cate1 = inits.isInitialized("cate1") ? new com.lotteon.entity.category.QCategoryArticle(forProperty("cate1"), inits.get("cate1")) : null;
        this.cate2 = inits.isInitialized("cate2") ? new com.lotteon.entity.category.QCategoryArticle(forProperty("cate2"), inits.get("cate2")) : null;
        this.member = inits.isInitialized("member") ? new com.lotteon.entity.member.QMember(forProperty("member"), inits.get("member")) : null;
        this.seller = inits.isInitialized("seller") ? new com.lotteon.entity.member.QSeller(forProperty("seller"), inits.get("seller")) : null;
    }

}

