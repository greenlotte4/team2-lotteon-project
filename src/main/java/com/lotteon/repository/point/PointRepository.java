package com.lotteon.repository.point;

import com.lotteon.entity.member.Customer;
import com.lotteon.entity.point.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findAllByCustomer(Customer customer);

    Page<Point> findAllByCustomer(Customer customer, Pageable pageable);

    Page<Point> findAllByCustomerOrderByPointExpiration(Customer customer, Pageable pageable);

    Page<Point> findAllByCustomer_CustName(String custName, Pageable pageable);

    Page<Point> findAllByCustomer_CustEmail(String custEmail, Pageable pageable);

    Page<Point> findAllByCustomer_CustHp(String custHp, Pageable pageable);

    List<Point> findAllByCustomerAndPointTypeOrderByPointExpirationAsc(Customer customer, int i);

    Page<Point> findAllByCustomerAndPointTypeOrderByPointExpirationAsc(Customer customer, int i, Pageable pageable);

    List<Point> findAllByPointExpirationBefore(LocalDate today);

    List<Point> findAllByCustomerAndPointType(Customer customer, int i);

    Page<Point> findAllByOrderByPointRdateDesc(Pageable pageable);

    Page<Point> findAllByCustomer_Member_IdOrderByPointRdateDesc(Long id, Pageable pageable);

    Page<Point> findAllByCustomer_CustNameOrderByPointRdateDesc(String custName, Pageable pageable);

    Page<Point> findAllByCustomer_CustEmailOrderByPointRdateDesc(String custEmail, Pageable pageable);

    Page<Point> findAllByCustomer_CustHpOrderByPointRdateDesc(String custHp, Pageable pageable);

    List<Point> findAllByPointType(int i);

    Page<Point> findAllByCustomerAndPointTypeAndPointRdateBetweenOrderByPointExpirationAsc(Customer customer, int i, LocalDate varDay, LocalDate today, Pageable pageable);
  
    List<Point> findAllByPointRdateBefore(LocalDate twoYear);

    Page<Point> findAllByCustomerAndPointTypeOrderByPointRdateDesc(Customer customer, int i, Pageable pageable);

    Page<Point> findAllByCustomerAndPointTypeAndPointRdateBetweenOrderByPointRdateDesc(Customer customer, int i, LocalDate varDay, LocalDate today, Pageable pageable);

    List<Point> findAllByCustomer_IdAndPointTypeAndPointUdateBetween(Long custId, int i, LocalDateTime before, LocalDateTime after);

    Optional<Point> findFirstByOrderIdAndPointEtc(Long orderId, String s);

    Optional<Point> findFirstByOrderIdAndPointType(Long orderId, int i);
}
