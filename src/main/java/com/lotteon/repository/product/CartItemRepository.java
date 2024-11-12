package com.lotteon.repository.product;

import com.lotteon.entity.product.Cart;
import com.lotteon.entity.product.CartItem;
import com.lotteon.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
//    List<CartItem> findByCartAndProductId(Cart cart, long prodId);


    List<CartItem> findAllByCartAndProduct(Cart cart, Product prod);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    Optional<CartItem> findByProduct(Product product);

    List<CartItem> findAllByCart(Cart cart);
}
