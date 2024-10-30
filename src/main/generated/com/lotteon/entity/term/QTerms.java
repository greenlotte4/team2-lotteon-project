package com.lotteon.entity.term;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTerms is a Querydsl query type for Terms
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTerms extends EntityPathBase<Terms> {

    private static final long serialVersionUID = -1265628074L;

    public static final QTerms terms = new QTerms("terms");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath termsContent = createString("termsContent");

    public final StringPath termsName = createString("termsName");

    public final StringPath termsTitle = createString("termsTitle");

    public final StringPath termsType = createString("termsType");

    public QTerms(String variable) {
        super(Terms.class, forVariable(variable));
    }

    public QTerms(Path<? extends Terms> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTerms(PathMetadata metadata) {
        super(Terms.class, metadata);
    }

}

