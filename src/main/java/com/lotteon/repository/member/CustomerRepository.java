package com.lotteon.repository.member;

import com.lotteon.entity.member.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByMemberId(Long memberId);

    Customer findByMember_MemUid(String keyword);

    Customer findByCustName(String keyword);

    Customer findByCustEmail(String keyword);

    Customer findByCustHp(String keyword);
}
