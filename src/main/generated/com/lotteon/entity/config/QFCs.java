package com.lotteon.entity.config;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFCs is a Querydsl query type for FCs
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFCs extends EntityPathBase<FCs> {

    private static final long serialVersionUID = -1708421733L;

    public static final QFCs fCs = new QFCs("fCs");

    public final StringPath call = createString("call");

    public final StringPath email = createString("email");

    public final StringPath etime = createString("etime");

    public final StringPath hp = createString("hp");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath stime = createString("stime");

    public QFCs(String variable) {
        super(FCs.class, forVariable(variable));
    }

    public QFCs(Path<? extends FCs> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFCs(PathMetadata metadata) {
        super(FCs.class, metadata);
    }

}

