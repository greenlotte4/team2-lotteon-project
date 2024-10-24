package com.lotteon.entity.config;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBanner is a Querydsl query type for Banner
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBanner extends EntityPathBase<Banner> {

    private static final long serialVersionUID = -316241177L;

    public static final QBanner banner = new QBanner("banner");

    public final StringPath bannerBg = createString("bannerBg");

    public final DateTimePath<java.sql.Timestamp> bannerEdate = createDateTime("bannerEdate", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> bannerEtime = createDateTime("bannerEtime", java.sql.Timestamp.class);

    public final StringPath bannerImg = createString("bannerImg");

    public final StringPath bannerLink = createString("bannerLink");

    public final NumberPath<Integer> bannerLocation = createNumber("bannerLocation", Integer.class);

    public final StringPath bannerName = createString("bannerName");

    public final DateTimePath<java.sql.Timestamp> bannerSdate = createDateTime("bannerSdate", java.sql.Timestamp.class);

    public final StringPath bannerSize = createString("bannerSize");

    public final NumberPath<Integer> bannerState = createNumber("bannerState", Integer.class);

    public final DateTimePath<java.sql.Timestamp> bannerStime = createDateTime("bannerStime", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBanner(String variable) {
        super(Banner.class, forVariable(variable));
    }

    public QBanner(Path<? extends Banner> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBanner(PathMetadata metadata) {
        super(Banner.class, metadata);
    }

}

