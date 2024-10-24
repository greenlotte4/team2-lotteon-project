package com.lotteon.repository.member;

import com.lotteon.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemUid(String username);
    List<Member> findAllByMemRole(String role);
    Optional<Member> findByMemRole(String role);

    Page<Member> findAllByMemRole(String customer, Pageable pageable);

    Page<Member> findAllByMemRoleOrderByIdDesc(String customer, Pageable pageable);
}
