package com.lotteon.entity.point;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPoint is a Querydsl query type for Point
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPoint extends EntityPathBase<Point> {

    private static final long serialVersionUID = 2066290369L;

    public static final QPoint point = new QPoint("point");

    public final NumberPath<Long> custId = createNumber("custId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final StringPath pointEtc = createString("pointEtc");

    public final DatePath<java.time.LocalDate> pointExpiration = createDate("pointExpiration", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> pointRdate = createDate("pointRdate", java.time.LocalDate.class);

    public final NumberPath<Integer> pointType = createNumber("pointType", Integer.class);

    public final NumberPath<Integer> pointVar = createNumber("pointVar", Integer.class);

    public QPoint(String variable) {
        super(Point.class, forVariable(variable));
    }

    public QPoint(Path<? extends Point> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPoint(PathMetadata metadata) {
        super(Point.class, metadata);
    }

}

