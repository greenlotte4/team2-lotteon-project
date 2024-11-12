package com.lotteon.repository.point;

import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.point.Coupon;
import com.lotteon.entity.point.CustomerCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerCouponRepository extends JpaRepository<CustomerCoupon, Long> {
    Optional<CustomerCoupon> findByCustomerAndCoupon(Customer customer, Coupon coupon);

    List<CustomerCoupon> findAllByCoupon(Coupon coupon);

//    Page<CustomerCoupon> findAllByOrderByCouponStateAscCouponUDateDesc(Pageable pageable);

    Page<CustomerCoupon> findAllByIdOrderByCouponStateAscCouponUDateDesc(Long id, Pageable pageable);

    Page<CustomerCoupon> findAllByCouponOrderByCouponStateAscIdDesc(Coupon coupon, Pageable pageable);

    Page<CustomerCoupon> findAllByCustomer(Customer customer, Pageable pageable);

    Page<CustomerCoupon> findAllByCouponAndIdOrderByCouponStateAscIdDesc(Coupon coupon, long l, Pageable pageable);

    Page<CustomerCoupon> findAllByCouponAndCouponIdOrderByCouponStateAscIdDesc(Coupon coupon, long l, Pageable pageable);

    Page<CustomerCoupon> findAllByCouponAndCustomerOrderByCouponStateAscIdDesc(Coupon coupon, Customer customer, Pageable pageable);

    int countCustomerCouponsByCustomer(Customer customer);


    Page<CustomerCoupon> findAllByOrderByCouponStateAscIdDesc(Pageable pageable);

    Page<CustomerCoupon> findAllByIdOrderByCouponStateAscIdDesc(long l, Pageable pageable);

    List<CustomerCoupon> findAllByCouponState(int i);

    List<CustomerCoupon> findAllByCustomerAndCoupon_Member(Customer customer, Member seller);

    List<CustomerCoupon> findAllByCustomer(Customer customer);

    List<CustomerCoupon> findAllByCustomerAndCoupon_Member_MemRole(Customer customer, String admin);

    List<CustomerCoupon> findAllByCustomerAndCoupon_MemberAndCouponState(Customer customer, Member member, int i);

    List<CustomerCoupon> findAllByCustomerAndCoupon_Member_MemRoleAndCouponState(Customer customer, String admin, int i);

    Page<CustomerCoupon> findAllByCustomerAndCouponState(Customer customer, int i, Pageable pageable);
}
