package com.lotteon.repository.product;

import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.product.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findAllByCustomer(Customer customer, Pageable pageable);

    Page<Order> findAllByOrderItems_Seller(Seller seller,Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByOrderItems_SellerAndOrderItems_OrderDeliIdIsNotNullAndOrderItems_OrderDeliCompanyNotNullOrderByIdDesc(Seller seller, Pageable pageable);

    Page<Order> findAllByOrderItems_SellerAndOrderItems_OrderDeliIdAndOrderItems_OrderDeliCompanyNotNullOrderByIdDesc(Seller seller, String keyword, Pageable pageable);

    Page<Order> findAllByOrderItems_SellerAndIdAndOrderItems_OrderDeliIdIsNotNullAndOrderItems_OrderDeliCompanyNotNullOrderByIdDesc(Seller seller, Long keyword, Pageable pageable);

    Page<Order> findAllByOrderItems_SellerAndReceiverNameAndOrderItems_OrderDeliIdIsNotNullAndOrderItems_OrderDeliCompanyNotNullOrderByIdDesc(Seller seller, String keyword, Pageable pageable);

    Optional<Order> findByOrderItems_SellerAndOrderItems_OrderDeliIdAndOrderItems_OrderDeliCompanyNotNull(Seller seller, String deliveryId);

    Page<Order> findAllByCustomerAndOrderRdateBetweenOrderByIdAsc(Customer customer, Timestamp startDate, Timestamp endDate, Pageable pageable);

   }
