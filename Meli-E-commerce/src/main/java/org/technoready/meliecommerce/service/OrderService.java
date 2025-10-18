package org.technoready.meliecommerce.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.technoready.meliecommerce.dto.OrderDetailsDTO;
import org.technoready.meliecommerce.dto.OrderResponseDTO;
import org.technoready.meliecommerce.entity.Order;
import org.technoready.meliecommerce.entity.OrderDetails;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.exception.InactiveResourceException;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.repository.OrderRepository;
import org.technoready.meliecommerce.repository.ProductRepository;
import org.technoready.meliecommerce.repository.UserRepository;
import org.technoready.meliecommerce.util.MapperUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(Long userId, List<OrderDetailsDTO> detailsRequest) {
        log.info("Creating order for user {}", userId);

        User user =  validateUserId(userId);

        Order order = new Order();
        order.setUser(user);

        List<OrderDetails> details = new ArrayList<>();
        double total = 0;

        for (OrderDetailsDTO detailReq : detailsRequest) {
            Product product = productRepository.findById(detailReq.getProductId())
                    .orElseThrow(() -> {
                        log.error("Product not found with id: {}", detailReq.getProductId());
                        return new ResourceNotFoundException("Product", "id", detailReq.getProductId());
                    });

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

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id {}", savedOrder.getId());

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        log.info("Getting all orders");
        List<Order> orders = orderRepository.findAll();
        log.info("Getting {} orders", orders.size());
        return orders;
    }

    public Optional<Order> getOrderById(Long id) {
        log.info("Retrieving order with id: {}", id);
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            log.warn("Order not found with id: {}", id);
        }
        return order;
    }

    public List<Order> getOrdersByUserIdActive(Long userId) {
        log.info("Retrieving active orders for user with id: {}", userId);
        User user =  validateUserId(userId);
        List<Order> orders = orderRepository.findByUser_IdAndActiveTrue(user.getId());

        if(orders.isEmpty()) {
            log.warn("Not actives orders found for user with id: {}", userId);
        }

        log.info("Retrieved {} active orders for user: {}", orders.size(), user.getId());
        return orders;
    }

    public List<Order> getOrdersByUserId(Long userId){
        log.info("Retrieving all orders for user with id: {}", userId);
        validateUserId(userId);
        List<Order> orders = orderRepository.findByUserId(userId);

        if(orders.isEmpty()) {
            log.warn("Not actives orders found for user with id: {}", userId);
        }

        log.info("Retrieved {} orders for user: {}", orders.size(), userId);
        return orders;
    }

    public void deleteOrder(Long id) {
        log.info("Attempting to delete order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot delete - Order not found with id: {}", id);
                    return new ResourceNotFoundException("Order", "id", id);
                });

        order.setActive(false);
        orderRepository.save(order);
        log.info("Order with id: {} has been successfully deactivated", id);
    }

    public List<Order> getAllActiveOrders() {
        log.info("Retrieving all active orders");
        List<Order> orders = orderRepository.findOrdersByActiveTrue();
        log.info("Retrieved {} active orders", orders.size());
        return orders;
    }


    @Transactional
    public OrderResponseDTO updateOrder(Long id, List<OrderDetailsDTO> orderDetailsDTO) {
        log.info("Attempting to update order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot update - Order not found with id: {}", id);
                    return new ResourceNotFoundException("Order", "id", id);
                });

        if (!order.isActive()) {
            log.error("Cannot update inactive order with id: {}", id);
            throw new InactiveResourceException("Order", id);
        }

        order.getDetails().clear();

        double total = 0;
        List<OrderDetails> updatedDetails = new ArrayList<>();

        for (OrderDetailsDTO detailReq : orderDetailsDTO) {
            Product product = productRepository.findById(detailReq.getProductId())
                    .orElseThrow(() -> {
                        log.error("Product not found with id: {}", detailReq.getProductId());
                        return new ResourceNotFoundException("Product", "id", detailReq.getProductId());
                    });

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
        Order updatedOrder = orderRepository.save(order);

        log.info("Order with id: {} has been successfully updated", id);

        return MapperUtil.toDTO(updatedOrder);
    }

    public User validateUserId(Long userId) {
        log.info("Validating user with id {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id {}", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });

        return user;
    }



}

