package com.lotteon.repository.product;

import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.product.Order;
import com.lotteon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    boolean existsByCustomerAndOrderItems_Product(Customer customer, Product product);

    Page<Order> findAllByIdOrderByIdDesc(long l, Pageable pageable);

    Page<Order> findAllByCustomer_CustNameOrderByIdDesc(String keyword, Pageable pageable);

    Page<Order> findAllByCustomer_Member_MemUidOrderByIdDesc(String keyword, Pageable pageable);

    Page<Order> findAllByOrderItems_SellerAndCustomer_Member_MemUidOrderByIdDesc(Seller seller, String keyword, Pageable pageable);

    Page<Order> findAllByIdAndOrderItems_SellerOrderByIdDesc(long l, Seller seller, Pageable pageable);

    Page<Order> findAllByOrderItems_SellerAndCustomer_CustNameOrderByIdDesc(Seller seller, String keyword, Pageable pageable);

    Page<Order> findAllByCustomerAndOrderRdateBetweenOrderByIdDesc(Customer customer, Timestamp varDay, Timestamp today, Pageable pageable);

    List<Order> findAllByOrderRdateAfter(Timestamp threeDaysAgo);

    Optional<Order> findByIdAndOrderItems_State2(Long orderId, int i);

    Long countByCustomer(Customer customer);

    List<Order> findAllByOrderStateAndCustomer(int i, Customer customer);

    List<Order> findAllByOrderItems_State2AndCustomer(int i, Customer customer);
}
