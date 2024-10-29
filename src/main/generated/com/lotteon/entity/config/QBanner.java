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

    public final DatePath<java.sql.Date> bannerEdate = createDate("bannerEdate", java.sql.Date.class);

    public final TimePath<java.sql.Time> bannerEtime = createTime("bannerEtime", java.sql.Time.class);

    public final StringPath bannerImg = createString("bannerImg");

    public final StringPath bannerLink = createString("bannerLink");

    public final NumberPath<Integer> bannerLocation = createNumber("bannerLocation", Integer.class);

    public final StringPath bannerName = createString("bannerName");

    public final DatePath<java.sql.Date> bannerSdate = createDate("bannerSdate", java.sql.Date.class);

    public final StringPath bannerSize = createString("bannerSize");

    public final NumberPath<Integer> bannerState = createNumber("bannerState", Integer.class);

    public final TimePath<java.sql.Time> bannerStime = createTime("bannerStime", java.sql.Time.class);

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

