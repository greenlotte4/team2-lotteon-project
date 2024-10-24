package com.lotteon.entity.config;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCopyright is a Querydsl query type for Copyright
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCopyright extends EntityPathBase<Copyright> {

    private static final long serialVersionUID = 941197868L;

    public static final QCopyright copyright = new QCopyright("copyright");

    public final StringPath copy = createString("copy");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QCopyright(String variable) {
        super(Copyright.class, forVariable(variable));
    }

    public QCopyright(Path<? extends Copyright> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCopyright(PathMetadata metadata) {
        super(Copyright.class, metadata);
    }

}

