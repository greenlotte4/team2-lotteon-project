package com.lotteon.service.event;

import com.lotteon.config.MyUserDetails;
import com.lotteon.entity.member.AttendanceEvent;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.point.Coupon;
import com.lotteon.entity.point.Point;
import com.lotteon.repository.member.AttendanceEventRepository;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.point.PointRepository;
import com.lotteon.service.member.CustomerService;
import com.lotteon.service.point.CustomerCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final AttendanceEventRepository attendanceEventRepository;
    private final PointRepository pointRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final CustomerCouponService customerCouponService;

    public String updateEvent() {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();

        AttendanceEvent event = attendanceEventRepository.findByCustomer(customer);
        if (event == null) {
            event = this.insertEvent(event,customer);
            attendanceEventRepository.save(event);
        }
        if(event.getAttendanceToday()==1){
            return "done";
        }
        if(event.getAttendanceSequence()==2){
            return "complete";
        }

        if(event.getAttendanceSequence()==1){
            event.updateEventReset();
            attendanceEventRepository.save(event);
        } else if(event.getAttendanceSequence()==0){
            event.updateEventIncrement();
            attendanceEventRepository.save(event);
        }
        if(event.getAttendanceDays()==7){
            event.updateEventComplete();
            attendanceEventRepository.save(event);
            return "coupon";
        }
        return "advance";

    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetEventSequence(){
        List<AttendanceEvent> attendanceEvents = attendanceEventRepository.findByAttendanceSequenceIn(Arrays.asList(0, 1));
        for(AttendanceEvent event : attendanceEvents){
            if(event.getAttendanceSequence()==1){
                event.updateEventSequenceZero();

            } else if(event.getAttendanceSequence()==0){
                event.updateEventSequenceOne();
            }
        }
        attendanceEventRepository.saveAll(attendanceEvents);

    }

    private AttendanceEvent insertEvent(AttendanceEvent event,Customer customer) {
        return event = AttendanceEvent.builder()
                .attendanceSequence(0)
                .customer(customer)
                .attendanceDays(0)
                .attendanceState(0)
                .attendanceMiddleState(0)
                .attendanceToday(0)
                .build();
    }


    public int findEvent(String result) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        AttendanceEvent newEvent = attendanceEventRepository.findByCustomer(customer);
        if(result.equals("done")){
            AttendanceEvent event = attendanceEventRepository.findByCustomer(customer);
            int count = event.getAttendanceDays();
            return count;
        }
        if(result.equals("complete")){
            this.updateCustomerPoint(100);
            return 7;
        } else if(result.equals("coupon")){
            customerCouponService.postCustCoupon();
            newEvent.updateEventSequenceEnd();
            attendanceEventRepository.save(newEvent);
            return 7;
        } else {
            AttendanceEvent event = attendanceEventRepository.findByCustomer(customer);
            int count = event.getAttendanceDays();

            this.updateCustomerPointOrCoupon(count,customer);
            return count;
        }

    }

    private void updateCustomerPointOrCoupon(int count, Customer customer) {
        AttendanceEvent event = attendanceEventRepository.findByCustomer(customer);
        if(count==5){
            if(event.getAttendanceMiddleState()==0){
                this.updateCustomerPoint(2000);
                event.updateEventMiddleState();
                attendanceEventRepository.save(event);
            } else {
                this.updateCustomerPoint(100);
            }
        } else {
            this.updateCustomerPoint(100);
        }
    }

    private void updateCustomerPoint(int value) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        LocalDate today = LocalDate.now().plusMonths(2);

        Customer customer = auth.getUser().getCustomer();

        Point point = Point.builder()
                .pointVar(value)
                .pointType(1)
                .pointEtc("출석이벤트")
                .custId(customer.getId())
                .pointExpiration(today)
                .build();
        pointRepository.save(point);

        int points = customerService.updateCustomerPoint(customer);
        customer.updatePoint(points);
        customerRepository.save(customer);
    }
}
