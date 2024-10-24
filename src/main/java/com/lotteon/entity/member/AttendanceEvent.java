package com.lotteon.entity.member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance_event")
public class AttendanceEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attendance_days")
    private int attendanceDays;

    @Column(name = "attendance_sequence")
    private int attendanceSequence;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id")
    private Customer customer;

    @Column(name = "attendance_state")
    private int attendanceState;

    @Column(name = "attendance_middle_state")
    private int attendanceMiddleState;

    @Column(name = "attendance_today")
    private int attendanceToday;

    public void updateEventReset(){
        this.attendanceDays = 1;
        this.attendanceSequence = 1;
        this.attendanceToday = 1;
    }

    public void updateEventIncrement(){
        this.attendanceDays++;
        this.attendanceSequence = 1;
        this.attendanceToday = 1;
    }

    public void updateEventComplete(){
        this.attendanceState = 1;
        this.attendanceToday = 1;
    }

    public void updateEventSequenceEnd() {
        this.attendanceSequence = 2;
    }

    public void updateEventSequenceZero() {
        this.attendanceSequence = 0;
        this.attendanceToday = 0;
    }

    public void updateEventSequenceOne() {
        this.attendanceSequence = 1;
        this.attendanceToday = 0;
    }

    public void updateEventMiddleState() {
        this.attendanceMiddleState = 1;
    }
}
