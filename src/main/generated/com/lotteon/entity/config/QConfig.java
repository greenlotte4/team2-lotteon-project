package com.lotteon.entity.config;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QConfig is a Querydsl query type for Config
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConfig extends EntityPathBase<Config> {

    private static final long serialVersionUID = -274690307L;

    public static final QConfig config = new QConfig("config");

    public final DateTimePath<java.sql.Timestamp> configCreatedAt = createDateTime("configCreatedAt", java.sql.Timestamp.class);

    public final StringPath configFabicon = createString("configFabicon");

    public final StringPath configFooterLogo = createString("configFooterLogo");

    public final StringPath configHeaderLogo = createString("configHeaderLogo");

    public final BooleanPath configIsUsed = createBoolean("configIsUsed");

    public final StringPath configSub = createString("configSub");

    public final StringPath configTitle = createString("configTitle");

    public final StringPath configUpdatedAdmin = createString("configUpdatedAdmin");

    public final NumberPath<Integer> configUpdateLocation = createNumber("configUpdateLocation", Integer.class);

    public final NumberPath<Integer> configUpdateVersion = createNumber("configUpdateVersion", Integer.class);

    public final StringPath configVersion = createString("configVersion");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QConfig(String variable) {
        super(Config.class, forVariable(variable));
    }

    public QConfig(Path<? extends Config> path) {
        super(path.getType(), path.getMetadata());
    }

    public QConfig(PathMetadata metadata) {
        super(Config.class, metadata);
    }

}

