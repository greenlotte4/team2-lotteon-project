package com.lotteon.repository.point;

import com.lotteon.entity.point.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findAllByCustId(Long id);

    Page<Point> findAllByCustId(Long id, Pageable pageable);

    Page<Point> findAllByCustIdAndPointRdateBetweenOrderByPointRdateAsc(Long id, LocalDate varDay, LocalDate today, Pageable pageable);

    Page<Point> findAllByCustIdAndPointRdateBetweenOrderByPointExpirationAsc(Long id, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Point> findAllByCustIdOrderByPointExpiration(Long id, Pageable pageable);
}
