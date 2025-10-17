package org.technoready.meliecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.technoready.meliecommerce.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByActiveTrue();
}
