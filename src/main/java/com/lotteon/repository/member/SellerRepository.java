package com.lotteon.repository.member;

import com.lotteon.entity.member.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findBySellCompany(String keyword);


    Optional<Seller> findBySellBusinessCodeAndSellEmail(String name, String email);

    Optional<Seller> findBySellBusinessCodeAndSellEmailAndMember_MemUid(String name, String email, String uid);

    Optional<Seller> findByMember_MemUid(String keyword);

    Page<Seller> findAllByOrderByMember_MemRdateDesc(Pageable pageable);

    Page<Seller> findAllBySellCompanyOrderByMember_MemRdateDesc(String keyword, Pageable pageable);

    Page<Seller> findAllBySellRepresentativeOrderByMember_MemRdateDesc(String keyword, Pageable pageable);

    Page<Seller> findAllBySellBusinessCodeOrderByMember_MemRdateDesc(String keyword, Pageable pageable);

    Page<Seller> findAllBySellHpOrderByMember_MemRdateDesc(String keyword, Pageable pageable);
}
