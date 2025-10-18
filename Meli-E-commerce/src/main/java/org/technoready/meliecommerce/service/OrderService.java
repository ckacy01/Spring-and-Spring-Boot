package org.technoready.meliecommerce.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.technoready.meliecommerce.dto.OrderDetailsDTO;
import org.technoready.meliecommerce.dto.OrderResponseDTO;
import org.technoready.meliecommerce.entity.Order;
import org.technoready.meliecommerce.entity.OrderDetails;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.repository.OrderRepository;
import org.technoready.meliecommerce.repository.ProductRepository;
import org.technoready.meliecommerce.repository.UserRepository;
import org.technoready.meliecommerce.util.MapperUtil;

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
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);

        List<OrderDetails> details = new ArrayList<>();
        double total = 0;

        for (OrderDetailsDTO detailReq : detailsRequest) {
            Product product = productRepository.findById(detailReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

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

    public List<Order> getOrdersByUserIdActive(Long userId) {
        return orderRepository.findByUser_IdAndActiveTrue(userId);
    }

    public List<Order> getOrdersByUserId(long id){
        return orderRepository.findByUserId(id);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setActive(false);
        orderRepository.save(order);
    }

    public List<Order> getAllActiveOrders() {
        return orderRepository.findOrdersByActiveTrue();
    }


    @Transactional
    public OrderResponseDTO updateOrder(Long id, List<OrderDetailsDTO> orderDetailsDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.isActive()) {
            throw new RuntimeException("Cannot update an inactive order");
        }

        order.getDetails().clear();

        double total = 0;
        List<OrderDetails> updatedDetails = new ArrayList<>();

        for (OrderDetailsDTO detailReq : orderDetailsDTO) {
            Product product = productRepository.findById(detailReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderDetails detail = new OrderDetails();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setProductName(product.getName());
            detail.setDescriptionSnap(product.getDescription());
            detail.setQuantity(detailReq.getQuantity());
            detail.setUnitPrice(product.getPrice());

            total += product.getPrice() * detailReq.getQuantity();
            updatedDetails.add(detail);
        }

        order.setDetails(updatedDetails);
        order.setTotal(total);
        Order order1 = orderRepository.save(order);
        return MapperUtil.toDTO(order1);
    }


}

