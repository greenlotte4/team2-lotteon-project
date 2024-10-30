package com.lotteon.entity.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAttendanceEvent is a Querydsl query type for AttendanceEvent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAttendanceEvent extends EntityPathBase<AttendanceEvent> {

    private static final long serialVersionUID = 509368110L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAttendanceEvent attendanceEvent = new QAttendanceEvent("attendanceEvent");

    public final NumberPath<Integer> attendanceDays = createNumber("attendanceDays", Integer.class);

    public final NumberPath<Integer> attendanceMiddleState = createNumber("attendanceMiddleState", Integer.class);

    public final NumberPath<Integer> attendanceSequence = createNumber("attendanceSequence", Integer.class);

    public final NumberPath<Integer> attendanceState = createNumber("attendanceState", Integer.class);

    public final NumberPath<Integer> attendanceToday = createNumber("attendanceToday", Integer.class);

    public final QCustomer customer;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QAttendanceEvent(String variable) {
        this(AttendanceEvent.class, forVariable(variable), INITS);
    }

    public QAttendanceEvent(Path<? extends AttendanceEvent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAttendanceEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAttendanceEvent(PathMetadata metadata, PathInits inits) {
        this(AttendanceEvent.class, metadata, inits);
    }

    public QAttendanceEvent(Class<? extends AttendanceEvent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new QCustomer(forProperty("customer"), inits.get("customer")) : null;
    }

}

