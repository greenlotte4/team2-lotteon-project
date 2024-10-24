package com.lotteon.entity.config;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVersion is a Querydsl query type for Version
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVersion extends EntityPathBase<Version> {

    private static final long serialVersionUID = -524973955L;

    public static final QVersion version = new QVersion("version");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> mem_id = createNumber("mem_id", Long.class);

    public final StringPath verContent = createString("verContent");

    public final StringPath verName = createString("verName");

    public final DateTimePath<java.sql.Timestamp> verRdate = createDateTime("verRdate", java.sql.Timestamp.class);

    public QVersion(String variable) {
        super(Version.class, forVariable(variable));
    }

    public QVersion(Path<? extends Version> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVersion(PathMetadata metadata) {
        super(Version.class, metadata);
    }

}

