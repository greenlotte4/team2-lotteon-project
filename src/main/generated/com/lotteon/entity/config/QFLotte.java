package com.lotteon.entity.config;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFLotte is a Querydsl query type for FLotte
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFLotte extends EntityPathBase<FLotte> {

    private static final long serialVersionUID = -221082505L;

    public static final QFLotte fLotte = new QFLotte("fLotte");

    public final StringPath busiAddr = createString("busiAddr");

    public final StringPath busiCode = createString("busiCode");

    public final StringPath busiCompany = createString("busiCompany");

    public final StringPath busiOrderCode = createString("busiOrderCode");

    public final StringPath busiRepresentative = createString("busiRepresentative");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QFLotte(String variable) {
        super(FLotte.class, forVariable(variable));
    }

    public QFLotte(Path<? extends FLotte> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFLotte(PathMetadata metadata) {
        super(FLotte.class, metadata);
    }

}

