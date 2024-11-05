package com.lotteon.repository.member;

import com.lotteon.entity.member.Member;
import com.lotteon.entity.member.Seller;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface  MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemUid(String username);
    List<Member> findAllByMemRole(String memRole);
    Optional<Member> findByMemRole(String role);
    //List<String> roles = Arrays.asList("customer", "guest");
    List<Member> findAllByMemRoleIn(List<String> memRole);

    Page<Member> findAllByMemRole(String customer, Pageable pageable);

    // memRole이 특정 값들 중 하나에 해당하는 데이터를 id 내림차순으로 페이징하여 조회
    Page<Member> findAllByMemRoleInOrderByIdDesc(List<String> roles, Pageable pageable);

    // `customer`의 경우에만 수정 로직 진행
    Optional<Member> findById(Long id);

    Page<Member> findAllByMemRoleOrderByIdDesc(String customer, Pageable pageable);

    List<Member> findAllByMemLastLoginDateBefore(LocalDateTime standardDate);

    Optional<Member> findByCustomer_id(Long id);

    Optional<Member> findBySeller(Seller sellId);

    Optional<Member> findBySeller_SellCompany(String company);

    // 검색 기능 (아이디, 이름, 이메일, 휴대폰)
    Page<Member> findAllByMemUidOrderByIdDesc(String keyword, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.customer c WHERE  c.custName like :custName ORDER BY m.id DESC")
    Page<Member> findAllByCustNameOrderByIdDesc(@Param("custName") String custName, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.customer c WHERE c.custEmail = :custEmail ORDER BY m.id DESC")
    Page<Member> findAllByCustEmailOrderByIdDesc(@Param("custEmail") String custEmail, Pageable pageable);

    @Query("SELECT m FROM Member m JOIN m.customer c WHERE c.custHp = :custHp ORDER BY m.id DESC")
    Page<Member> findAllByCustHpOrderByIdDesc(@Param("custHp") String custHp, Pageable pageable);

    //Page<Member> findAllByCustNameOrderByIdDesc(String keyword, Pageable pageable);
    //Page<Member> findAllByCustEmailOrderByIdDesc(String keyword, Pageable pageable);
    //Page<Member> findAllByCustHpOrderByIdDesc(String keyword, Pageable pageable);
}
