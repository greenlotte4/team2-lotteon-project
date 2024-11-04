package com.lotteon.repository.point;

import com.lotteon.entity.member.Customer;
import com.lotteon.entity.point.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findAllByCustomer(Customer customer);

    Page<Point> findAllByCustomer(Customer customer, Pageable pageable);


    Page<Point> findAllByCustomerAndPointRdateBetweenOrderByPointExpirationAsc(Customer customer, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Point> findAllByCustomerOrderByPointExpiration(Customer customer, Pageable pageable);

    Page<Point> findAllByOrderById(Pageable pageable);

    Page<Point> findAllByCustomer_Member_IdOrderByIdDesc(Long id, Pageable pageable);

    Page<Point> findAllByCustomer_CustName(String custName, Pageable pageable);

    Page<Point> findAllByCustomer_CustEmail(String custEmail, Pageable pageable);

    Page<Point> findAllByCustomer_CustHp(String custHp, Pageable pageable);

    List<Point> findAllByCustomerAndPointTypeOrderByPointExpirationAsc(Customer customer, int i);
}
