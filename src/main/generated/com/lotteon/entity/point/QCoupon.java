package com.lotteon.entity.point;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = -741327595L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final NumberPath<Integer> couponBannerState = createNumber("couponBannerState", Integer.class);

    public final StringPath couponCaution = createString("couponCaution");

    public final NumberPath<Integer> couponDiscount = createNumber("couponDiscount", Integer.class);

    public final StringPath couponDiscountOption = createString("couponDiscountOption");

    public final StringPath couponExpiration = createString("couponExpiration");

    public final NumberPath<Integer> couponIssueCount = createNumber("couponIssueCount", Integer.class);

    public final NumberPath<Integer> couponMinPrice = createNumber("couponMinPrice", Integer.class);

    public final StringPath couponName = createString("couponName");

    public final DateTimePath<java.sql.Timestamp> couponRdate = createDateTime("couponRdate", java.sql.Timestamp.class);

    public final StringPath couponState = createString("couponState");

    public final StringPath couponType = createString("couponType");

    public final NumberPath<Integer> couponUseCount = createNumber("couponUseCount", Integer.class);

    public final NumberPath<Integer> customerCouponExpiration = createNumber("customerCouponExpiration", Integer.class);

    public final ListPath<CustomerCoupon, QCustomerCoupon> customerCoupons = this.<CustomerCoupon, QCustomerCoupon>createList("customerCoupons", CustomerCoupon.class, QCustomerCoupon.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.lotteon.entity.member.QMember member;

    public QCoupon(String variable) {
        this(Coupon.class, forVariable(variable), INITS);
    }

    public QCoupon(Path<? extends Coupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCoupon(PathMetadata metadata, PathInits inits) {
        this(Coupon.class, metadata, inits);
    }

    public QCoupon(Class<? extends Coupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.lotteon.entity.member.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

