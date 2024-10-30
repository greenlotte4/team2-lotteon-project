package com.lotteon.entity.point;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCustomerCoupon is a Querydsl query type for CustomerCoupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCustomerCoupon extends EntityPathBase<CustomerCoupon> {

    private static final long serialVersionUID = -975017069L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCustomerCoupon customerCoupon = new QCustomerCoupon("customerCoupon");

    public final QCoupon coupon;

    public final DatePath<java.time.LocalDate> couponExpiration = createDate("couponExpiration", java.time.LocalDate.class);

    public final NumberPath<Integer> couponState = createNumber("couponState", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> couponUDate = createDateTime("couponUDate", java.time.LocalDateTime.class);

    public final com.lotteon.entity.member.QCustomer customer;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QCustomerCoupon(String variable) {
        this(CustomerCoupon.class, forVariable(variable), INITS);
    }

    public QCustomerCoupon(Path<? extends CustomerCoupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCustomerCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCustomerCoupon(PathMetadata metadata, PathInits inits) {
        this(CustomerCoupon.class, metadata, inits);
    }

    public QCustomerCoupon(Class<? extends CustomerCoupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon"), inits.get("coupon")) : null;
        this.customer = inits.isInitialized("customer") ? new com.lotteon.entity.member.QCustomer(forProperty("customer"), inits.get("customer")) : null;
    }

}

