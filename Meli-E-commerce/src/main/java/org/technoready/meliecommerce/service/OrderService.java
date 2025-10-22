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

/**
 * Service class that handles business logic for order operations.
 * Manages order creation, retrieval, updating, and deletion with transaction support.
 * DATE: 18 - October - 2025
 *
 * @author Jorge Armando Avila Carrillo | NAOID: 3310
 * @version 1.0
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    /**
     * Creates a new order for the specified user with the provided order details.
     * Calculates the total price based on product prices and quantities.
     *
     * @param userId Long - The ID of the user creating the order
     * @param detailsRequest List<OrderDetailsDTO> - Details of products and quantities to order
     * @return Order - The created order entity
     * @throws ResourceNotFoundException if user or product is not found
     */
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

    /**
     * Retrieves all orders from the database.
     *
     * @return List<Order> - List of all orders
     */
    public List<Order> getAllOrders() {
        log.info("Getting all orders");
        List<Order> orders = orderRepository.findAll();
        log.info("Getting {} orders", orders.size());
        return orders;
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param id Long - The ID of the order
     * @return Optional<Order> - The order if found, empty otherwise
     */
    public Optional<Order> getOrderById(Long id) {
        log.info("Retrieving order with id: {}", id);
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            log.warn("Order not found with id: {}", id);
        }
        return order;
    }

    /**
     * Retrieves all active orders for a specific user.
     *
     * @param userId Long - The ID of the user
     * @return List<Order> - List of active orders for the user
     * @throws ResourceNotFoundException if user is not found
     */
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

    /**
     * Retrieves all orders (active and inactive) for a specific user.
     *
     * @param userId Long - The ID of the user
     * @return List<Order> - List of all orders for the user
     * @throws ResourceNotFoundException if user is not found
     */
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

    /**
     * Soft deletes an order by deactivating it.
     *
     * @param id Long - The ID of the order to delete
     * @throws ResourceNotFoundException if order is not found
     */
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

    /**
     * Retrieves all active orders from the database.
     *
     * @return List<Order> - List of active orders
     */
    public List<Order> getAllActiveOrders() {
        log.info("Retrieving all active orders");
        List<Order> orders = orderRepository.findOrdersByActiveTrue();
        log.info("Retrieved {} active orders", orders.size());
        return orders;
    }

    /**
     * Updates an existing order with new order details.
     * Clears existing details and replaces them with new ones, recalculating the total.
     *
     * @param id Long - The ID of the order to update
     * @param orderDetailsDTO List<OrderDetailsDTO> - New order details
     * @return OrderResponseDTO - The updated order data transfer object
     * @throws ResourceNotFoundException if order or product is not found
     * @throws InactiveResourceException if the order is inactive
     */
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

    /**
     * Validates that a user exists in the database.
     *
     * @param userId Long - The ID of the user to validate
     * @return User - The validated user entity
     * @throws ResourceNotFoundException if user is not found
     */
    public User validateUserId(Long userId) {
        log.info("Validating user with id {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id {}", userId);
                    return new ResourceNotFoundException("User", "id", userId);
                });
    }



}

