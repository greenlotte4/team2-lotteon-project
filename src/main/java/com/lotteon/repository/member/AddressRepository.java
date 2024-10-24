package com.lotteon.repository.member;

import com.lotteon.entity.member.Address;
import com.lotteon.entity.member.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByCustomer(Customer customer);
}
