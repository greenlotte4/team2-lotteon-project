package com.lotteon.repository.product;

import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.product.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findAllByCustomer(Customer customer, Pageable pageable);

    Page<Order> findAllByOrderItems_Seller(Seller seller,Pageable pageable);

    Page<Order> findAll(Pageable pageable);
}
