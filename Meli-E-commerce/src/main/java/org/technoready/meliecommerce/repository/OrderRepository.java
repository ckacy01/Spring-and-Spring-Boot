package org.technoready.meliecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.technoready.meliecommerce.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {}
