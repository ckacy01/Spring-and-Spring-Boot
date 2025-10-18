package org.technoready.meliecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.technoready.meliecommerce.entity.OrderDetails;

public interface OrderDetailRepository extends JpaRepository<OrderDetails, Long> {}
