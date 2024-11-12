package com.lotteon.service.point;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.cartOrder.OrderItemDto;
import com.lotteon.dto.responseDto.GetPointsDto;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.point.Point;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.point.PointRepository;
import com.lotteon.service.member.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {
    private final PointRepository pointRepository;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    @Scheduled(cron = "58 59 23 * * ?")
    public void updatePointState(){
        LocalDate today = LocalDate.now();
        List<Point> points = pointRepository.findAllByPointExpirationBefore(today);
        for(Point point : points){
            point.expirationPoint();
            pointRepository.save(point);
            int pointvar = customerService.updateCustomerPoint(point.getCustomer());
            point.getCustomer().updatePoint(pointvar);
            customerRepository.save(point.getCustomer());
        }
    }

    @Scheduled(cron = "30 59 23 * * ?")
    public void deletePoint(){
        LocalDate twoYear = LocalDate.now().minusYears(1);
        List<Point> points = pointRepository.findAllByPointRdateBefore(twoYear);
        pointRepository.deleteAll(points);
    }

    public Page<GetPointsDto> findAllByCustomer(int page) {
        Pageable pageable = PageRequest.of(page, 5);
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Customer customer = auth.getUser().getCustomer();

        Page<Point> points = pointRepository.findAllByCustomerAndPointTypeOrderByPointExpirationAsc(customer,1,pageable);
        Page<GetPointsDto> dtos = points.map(v->v.toGetPointsDto());

        return dtos;
    }

    public Page<GetPointsDto> findAllByCustomer2(int page) {
        Pageable pageable = PageRequest.of(page, 5);
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Customer customer = auth.getUser().getCustomer();

        Page<Point> points = pointRepository.findAllByCustomerAndPointTypeOrderByPointRdateDesc(customer,3,pageable);

        Page<GetPointsDto> dtos = points.map(Point::toGetPointsDto);

        return dtos;
    }

    public Page<GetPointsDto> findAllBySearch(int page, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, 5);
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        Page<Point> points;
        if(type.equals("date")){
            points = this.findAllByDate(pageable,customer,keyword);
        } else if(type.equals("custom")){
            points = this.findAllByCustom(pageable,customer,keyword);
        }else {
            points = this.findAllByMonth(pageable,customer,keyword);
        }
        Page<GetPointsDto> dtos = points.map(v->v.toGetPointsDto());
        return dtos;
    }

    public Page<GetPointsDto> findAllBySearch2(int page, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, 5);
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        Page<Point> points;
        if(type.equals("date")){
            points = this.findAllByDate2(pageable,customer,keyword);
        } else if(type.equals("custom")){
            points = this.findAllByCustom2(pageable,customer,keyword);
        }else {
            points = this.findAllByMonth2(pageable,customer,keyword);
        }
        Page<GetPointsDto> dtos = points.map(v->v.toGetPointsDto());
        return dtos;
    }

    private Page<Point> findAllByCustom(Pageable pageable, Customer customer, String keyword) {

        String sDate = keyword.substring(0,keyword.indexOf("~"));
        String eDate = keyword.substring(keyword.indexOf("~")+1);
        LocalDate startDate = LocalDate.parse(sDate);
        LocalDate endDate = LocalDate.parse(eDate);
        Page<Point> points = pointRepository.findAllByCustomerAndPointTypeAndPointRdateBetweenOrderByPointExpirationAsc(customer,1,startDate,endDate,pageable);
        return points;
    }

    private Page<Point> findAllByMonth(Pageable pageable, Customer customer, String keyword) {
        LocalDate today = LocalDate.now();
        LocalDate varDay = today.minusMonths(Integer.parseInt(keyword));
        Page<Point> points = pointRepository.findAllByCustomerAndPointTypeAndPointRdateBetweenOrderByPointExpirationAsc(customer,1,varDay,today,pageable);
        return points;
    }

    private Page<Point> findAllByDate(Pageable pageable, Customer customer, String keyword) {
        LocalDate today = LocalDate.now();
        LocalDate varDay = today.minusDays(Integer.parseInt(keyword));
        Page<Point> points = pointRepository.findAllByCustomerAndPointTypeAndPointRdateBetweenOrderByPointExpirationAsc(customer,1,varDay,today,pageable);
        return points;
    }

    private Page<Point> findAllByCustom2(Pageable pageable, Customer customer, String keyword) {

        String sDate = keyword.substring(0,keyword.indexOf("~"));
        String eDate = keyword.substring(keyword.indexOf("~")+1);
        LocalDate startDate = LocalDate.parse(sDate);
        LocalDate endDate = LocalDate.parse(eDate);
        Page<Point> points = pointRepository.findAllByCustomerAndPointTypeAndPointRdateBetweenOrderByPointRdateDesc(customer,3,startDate,endDate,pageable);

        return points;
    }

    private Page<Point> findAllByMonth2(Pageable pageable, Customer customer, String keyword) {
        LocalDate today = LocalDate.now();
        LocalDate varDay = today.minusMonths(Integer.parseInt(keyword));
        Page<Point> points = pointRepository.findAllByCustomerAndPointTypeAndPointRdateBetweenOrderByPointRdateDesc(customer,3,varDay,today,pageable);

        return points;
    }

    private Page<Point> findAllByDate2(Pageable pageable, Customer customer, String keyword) {
        LocalDate today = LocalDate.now();
        LocalDate varDay = today.minusDays(Integer.parseInt(keyword));
        Page<Point> points = pointRepository.findAllByCustomerAndPointTypeAndPointRdateBetweenOrderByPointRdateDesc(customer,3,varDay,today,pageable);

        return points;
    }

    public Page<GetPointsDto> findAll(int page) {
        Pageable pageable = PageRequest.of(page, 7);

        Page<Point> points = pointRepository.findAllByOrderByPointRdateDesc(pageable);
        Page<GetPointsDto> dtos = points.map(Point::toGetPointsDtoCustName);

        return dtos;
    }

    public Page<GetPointsDto> findAllByAdminSearch(int page, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Point> points;

        if(searchType.equals("uid")){
            points = this.findPointByUid(keyword,pageable);
        } else if(searchType.equals("name")){
            points = this.findPointByCustName(keyword,pageable);
        } else if(searchType.equals("email")){
            points = this.findPointByCustEmail(keyword,pageable);
        } else {
            points = this.findPointByCustHp(keyword,pageable);
        }
        Page<GetPointsDto> dtos = points.map(Point::toGetPointsDtoCustName);
        return dtos;
    }

    private Page<Point> findPointByCustHp(String keyword, Pageable pageable) {
        Customer customer = customerRepository.findByCustHp(keyword);
        Page<Point> points = pointRepository.findAllByCustomer_CustHpOrderByPointRdateDesc(customer.getCustHp(),pageable);
        return points;
    }

    private Page<Point> findPointByCustEmail(String keyword, Pageable pageable) {
        Customer customer = customerRepository.findByCustEmail(keyword);
        Page<Point> points = pointRepository.findAllByCustomer_CustEmailOrderByPointRdateDesc(customer.getCustEmail(),pageable);
        return points;
    }

    private Page<Point> findPointByCustName(String keyword, Pageable pageable) {
        Customer customer = customerRepository.findByCustName(keyword);
        Page<Point> points = pointRepository.findAllByCustomer_CustNameOrderByPointRdateDesc(customer.getCustName(),pageable);
        return points;
    }

    private Page<Point> findPointByUid(String keyword,Pageable pageable) {
        Customer customer = customerRepository.findByMember_MemUid(keyword);
        Page<Point> points = pointRepository.findAllByCustomer_Member_IdOrderByPointRdateDesc(customer.getMember().getId(),pageable);
        return points;
    }

    public void usePoint(Integer points) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        List<Point> pointList  = pointRepository.findAllByCustomerAndPointTypeOrderByPointExpirationAsc(customer,1);
        int remainingPoints = points;

        for (Point point : pointList) {
            if (remainingPoints <= 0) break;  // 필요한 포인트가 모두 차감되면 종료

            int availablePoints = point.getPointVar();

            if (availablePoints <= remainingPoints) {
                point.changePointVar(0);
                remainingPoints -= availablePoints;
            } else {
                // 필요한 포인트만 차감하고 종료
                point.changePointVar(0);


                Point point2 = Point.builder()
                        .pointEtc("사용후남은포인트")
                        .pointVar(point.getPointVar() - remainingPoints)
                        .pointExpiration(point.getPointExpiration())
                        .pointType(1)
                        .customer(point.getCustomer())
                        .orderId(point.getOrderId())
                        .build();
                pointRepository.save(point2);

                remainingPoints = 0;
            }

            // 변경된 포인트를 업데이트합니다.
            pointRepository.save(point);

            int points2 = customerService.updateCustomerPoint(customer);
            customer.updatePoint(points2);
            customerRepository.save(customer);

        }

    }

    public void usePoint2(Integer points, Long orderId) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        List<Point> pointList  = pointRepository.findAllByCustomerAndPointTypeOrderByPointExpirationAsc(customer,1);
        int remainingPoints = points;

        for (Point point : pointList) {
            if (remainingPoints <= 0) break;  // 필요한 포인트가 모두 차감되면 종료

            int availablePoints = point.getPointVar();

            if (availablePoints <= remainingPoints) {
                point.changePointVar(0);
                remainingPoints -= availablePoints;
            } else {
                point.changePointVar(0);


                Point point2 = Point.builder()
                        .pointEtc("사용후남은포인트")
                        .pointVar(point.getPointVar() - remainingPoints)
                        .pointExpiration(point.getPointExpiration())
                        .pointType(1)
                        .customer(point.getCustomer())
                        .orderId(orderId)
                        .build();
                pointRepository.save(point2);

                remainingPoints = 0;
            }


            int points2 = customerService.updateCustomerPoint(customer);
            customer.updatePoint(points2);
            customerRepository.save(customer);

        }
        Point usePoint = Point.builder()
                .pointEtc("상품 구매 포인트 사용")
                .pointVar(points)
                .pointType(3)
                .customer(customer)
                .orderId(orderId)
                .pointExpiration(LocalDate.now())
                .build();

        pointRepository.save(usePoint);

    }

}
