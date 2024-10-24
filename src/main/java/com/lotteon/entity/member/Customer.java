package com.lotteon.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Default;

import java.time.LocalDate;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_id")
    @ToString.Exclude
    private Member member;

    @Column(name = "cust_point")
    private Integer custPoint;

    @Column(name = "cust_addr")
    private String custAddr;

    @Column(name = "cust_name")
    private String custName;

    @Column(name = "cust_hp")
    private String custHp;

    @Column(name = "cust_gender")
    private Boolean custGender;

    @Column(name = "cust_email")
    private String custEmail;

    @Column(name = "cust_birth")
    private String custBirth;

    @Column(name = "cust_addr_option")
    private Boolean custAddrOption;

    @Column(name = "cust_term_option")
    private Boolean custTermOption;

    @Column(name = "cust_event_checker")
    private int custEventChecker;

    @Column(name = "cust_grade")
    private String custGrade;

    @OneToOne(mappedBy = "customer")
    @ToString.Exclude
    private AttendanceEvent attendanceEvent;

    public void updatePoint(int point){
        this.custPoint = point;
    }
}
