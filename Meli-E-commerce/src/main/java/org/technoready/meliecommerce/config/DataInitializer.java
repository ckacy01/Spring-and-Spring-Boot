package org.technoready.meliecommerce.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.repository.ProductRepository;
import org.technoready.meliecommerce.repository.UserRepository;

import java.util.List;
/**
 * Configuration class responsible for initializing database with default data.
 * Automatically creates sample products and users on application startup if the database is empty.
 * DATE: 18 - October - 2025
 *
 * @author Jorge Armando Avila Carrillo | NAOID: 3310
 * @version 1.0
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    @PostConstruct
    public void initData() {
        if (productRepository.count() == 0) {
            List<Product> products = List.of(
                    Product.builder().name("Laptop Gamer ASUS").description("Laptop con RTX 4060 y 16GB RAM").price(28999.99).active(true).build(),
                    Product.builder().name("iPhone 15 Pro").description("128GB, Titanio Azul").price(24999.99).active(true).build(),
                    Product.builder().name("Audífonos Sony WH-1000XM5").description("Cancelación de ruido premium").price(7499.99).active(true).build(),
                    Product.builder().name("Monitor LG UltraWide 34''").description("Resolución 3440x1440, HDR10").price(8999.99).active(true).build(),
                    Product.builder().name("Teclado Mecánico Keychron K6").description("Switches Red, inalámbrico").price(1799.99).active(true).build()
            );
            productRepository.saveAll(products);
            log.info("✅ Initial products successfully added");
        }

        if (userRepository.count() == 0) {
            List<User> users = List.of(
                    User.builder().name("Jorge").lastName("Avila").email("jorge@example.com").active(true).build(),
                    User.builder().name("Benjamin").lastName("Lopez").email("benjamin@example.com").active(true).build(),
                    User.builder().name("Jose").lastName("Perez").email("jose@example.com").active(true).build()
            );
            userRepository.saveAll(users);
            log.info("✅ Intial  users successfully added");
        }
    }
}

