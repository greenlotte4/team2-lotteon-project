package com.lotteon.entity.article;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFaq is a Querydsl query type for Faq
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFaq extends EntityPathBase<Faq> {

    private static final long serialVersionUID = -575260371L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFaq faq = new QFaq("faq");

    public final com.lotteon.entity.category.QCategoryArticle cate1;

    public final com.lotteon.entity.category.QCategoryArticle cate2;

    public final StringPath faqContent = createString("faqContent");

    public final DateTimePath<java.time.LocalDateTime> faqRdate = createDateTime("faqRdate", java.time.LocalDateTime.class);

    public final StringPath faqTitle = createString("faqTitle");

    public final NumberPath<Integer> faqViews = createNumber("faqViews", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.lotteon.entity.member.QMember member;

    public QFaq(String variable) {
        this(Faq.class, forVariable(variable), INITS);
    }

    public QFaq(Path<? extends Faq> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFaq(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFaq(PathMetadata metadata, PathInits inits) {
        this(Faq.class, metadata, inits);
    }

    public QFaq(Class<? extends Faq> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cate1 = inits.isInitialized("cate1") ? new com.lotteon.entity.category.QCategoryArticle(forProperty("cate1"), inits.get("cate1")) : null;
        this.cate2 = inits.isInitialized("cate2") ? new com.lotteon.entity.category.QCategoryArticle(forProperty("cate2"), inits.get("cate2")) : null;
        this.member = inits.isInitialized("member") ? new com.lotteon.entity.member.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

