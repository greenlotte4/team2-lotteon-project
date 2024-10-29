package com.lotteon.repository.member;

import com.lotteon.entity.member.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findBySellCompany(String keyword);


    Optional<Seller> findBySellBusinessCodeAndSellEmail(String name, String email);

    Optional<Seller> findBySellBusinessCodeAndSellEmailAndMember_MemUid(String name, String email, String uid);
}
