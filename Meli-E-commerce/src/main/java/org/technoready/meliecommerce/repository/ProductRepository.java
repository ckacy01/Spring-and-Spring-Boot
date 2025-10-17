package org.technoready.meliecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.technoready.meliecommerce.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
}
