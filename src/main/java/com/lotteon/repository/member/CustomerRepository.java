package com.lotteon.repository.member;

import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByMemberId(Long memberId);

    Optional<Customer> findById(Long id);

    Customer findByMember_MemUid(String keyword);

    Customer findByCustName(String keyword);

    Customer findByCustEmail(String keyword);

    Customer findByCustHp(String keyword);

    Optional<Customer> findByCustNameAndCustEmail(String name, String email);

    Optional<Customer> findByCustEmailAndCustName(String email, String email1);

    Optional<Customer> findByMember_MemUidAndCustEmail(String name, String email);

    Page<Customer> findAllByCustNameOrderByIdDesc(String keyword, Pageable pageable);

    Page<Customer> findAllByCustEmailOrderByIdDesc(String keyword, Pageable pageable);

    Page<Customer> findAllByCustHpOrderByIdDesc(String keyword, Pageable pageable);
}
