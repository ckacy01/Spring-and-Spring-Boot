package org.technoready.meliecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.technoready.meliecommerce.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(long id);
    List<Order> findByUser_IdAndActiveTrue(Long userId);
    List<Order> findOrdersByActiveTrue();
}
