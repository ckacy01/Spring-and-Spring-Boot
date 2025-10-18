package org.technoready.meliecommerce.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.technoready.meliecommerce.dto.OrderDetailsDTO;
import org.technoready.meliecommerce.entity.Order;
import org.technoready.meliecommerce.entity.OrderDetails;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.repository.OrderRepository;
import org.technoready.meliecommerce.repository.ProductRepository;
import org.technoready.meliecommerce.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(Long userId, List<OrderDetailsDTO> detailsRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = new Order();
        order.setUser(user);

        List<OrderDetails> details = new ArrayList<>();
        double total = 0;

        for (OrderDetailsDTO detailReq : detailsRequest) {
            Product product = productRepository.findById(detailReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            OrderDetails detail = new OrderDetails();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setProductName(product.getName());
            detail.setDescriptionSnap(product.getDescription());
            detail.setQuantity(detailReq.getQuantity());
            detail.setUnitPrice(product.getPrice());

            total += product.getPrice() * detailReq.getQuantity();
            details.add(detail);
        }

        order.setDetails(details);
        order.setTotal(total);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

}

