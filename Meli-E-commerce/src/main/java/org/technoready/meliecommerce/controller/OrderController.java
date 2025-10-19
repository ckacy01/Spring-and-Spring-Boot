package org.technoready.meliecommerce.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.technoready.meliecommerce.dto.OrderDetailsDTO;
import org.technoready.meliecommerce.dto.OrderResponseDTO;
import org.technoready.meliecommerce.dto.SuccessResponseDTO;
import org.technoready.meliecommerce.entity.Order;
import org.technoready.meliecommerce.exception.InactiveResourceException;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.service.OrderService;
import org.technoready.meliecommerce.util.MapperUtil;

import java.util.List;

/**
 * REST Controller that manages order-related operations.
 * Provides endpoints for creating, retrieving, updating, and deleting orders.
 * Handles order processing for users with their associated products.
 * DATE: 19 - October - 2025
 *
 * @author Jorge Armando Avila Carrillo | NAOID: 3310
 * @version 1.0
 */

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order for a specific user with the provided order details.
     *
     * @param userId Long - The ID of the user who is creating the order
     * @param detailsRequest List<OrderDetailsDTO> - List of order details containing product ID and quantity
     * @return ResponseEntity with SuccessResponseDTO containing the created OrderResponseDTO
     */

    @PostMapping("/{userId}")
    public ResponseEntity<SuccessResponseDTO<OrderResponseDTO>> createOrder(
            @PathVariable Long userId,
            @RequestBody List<OrderDetailsDTO> detailsRequest) {
        log.info("Controller: Received request to create order for user {}", userId);

        Order order = orderService.createOrder(userId, detailsRequest);
        OrderResponseDTO orderDTO = MapperUtil.toDTO(order);

        SuccessResponseDTO<OrderResponseDTO> response = SuccessResponseDTO.of(
                HttpStatus.CREATED.value(),
                String.format("Order created successfully with ID: %d", order.getId()),
                orderDTO
        );

        log.info("Controller: Order {} created successfully for user {}", order.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all orders or only active orders based on the activeOnly parameter.
     *
     * @param activeOnly boolean - Flag to retrieve only active orders (default: true)
     * @return ResponseEntity with SuccessResponseDTO containing list of OrderResponseDTOs
     */
    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<OrderResponseDTO>>> getOrders(
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {

        log.info("Controller: Received request to get all orders (activeOnly: {})", activeOnly);

        List<Order> orders = activeOnly
                ? orderService.getAllActiveOrders()
                : orderService.getAllOrders();

        List<OrderResponseDTO> orderDTOs = MapperUtil.toDTOList(orders);

        String message = activeOnly
                ? String.format("Retrieved %d active orders successfully", orders.size())
                : String.format("Retrieved %d orders successfully", orders.size());

        SuccessResponseDTO<List<OrderResponseDTO>> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                message,
                orderDTOs
        );

        log.info("Controller: Retrieved {} orders", orders.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all orders belonging to a specific user.
     *
     * @param userId Long - The ID of the user
     * @param activeOnly boolean - Flag to retrieve only active orders (default: true)
     * @return ResponseEntity with SuccessResponseDTO containing list of OrderResponseDTOs
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<SuccessResponseDTO<List<OrderResponseDTO>>> getOrdersByUser(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {

        String message;
        SuccessResponseDTO<List<OrderResponseDTO>> response;

        log.info("Controller: Received request to get orders for user {} (activeOnly: {})", userId, activeOnly);

        List<Order> orders = activeOnly
                ? orderService.getOrdersByUserIdActive(userId)
                : orderService.getOrdersByUserId(userId);

        List<OrderResponseDTO> orderDTOs = MapperUtil.toDTOList(orders);

        message = String.format("Retrieved %d orders for user %d successfully", orders.size(), userId);
        response = SuccessResponseDTO.of(
                    HttpStatus.OK.value(),
                    message,
                    orderDTOs);

        log.info("Controller: Retrieved {} orders for user {}", orders.size(), userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param id Long - The ID of the order to retrieve
     * @return ResponseEntity with SuccessResponseDTO containing the OrderResponseDTO
     * @throws ResourceNotFoundException if the order is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<OrderResponseDTO>> getOrderById(@PathVariable Long id) {
        log.info("Controller: Received request to get order {}", id);

        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> {
                    log.error("Controller: Order {} not found", id);
                    return new ResourceNotFoundException("Order", "id", id);
                });

        OrderResponseDTO orderDTO = MapperUtil.toDTO(order);

        SuccessResponseDTO<OrderResponseDTO> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("Order %d retrieved successfully", id),
                orderDTO
        );

        log.info("Controller: Order {} retrieved successfully", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft deletes (deactivates) an order by its ID.
     *
     * @param id Long - The ID of the order to delete
     * @return ResponseEntity with SuccessResponseDTO indicating successful deactivation
     * @throws ResourceNotFoundException if the order is not found
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<Void>> deleteOrder(@PathVariable Long id) {
        log.info("Controller: Received request to delete order {}", id);

        orderService.deleteOrder(id);

        SuccessResponseDTO<Void> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("Order %d has been successfully deactivated", id)
        );

        log.info("Controller: Order {} deleted successfully", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing order with new order details.
     *
     * @param id Long - The ID of the order to update
     * @param detailsRequest List<OrderDetailsDTO> - New list of order details
     * @return ResponseEntity with SuccessResponseDTO containing the updated OrderResponseDTO
     * @throws ResourceNotFoundException if the order is not found
     * @throws InactiveResourceException if the order is inactive
     */
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<OrderResponseDTO>> updateOrder(
            @PathVariable Long id,
            @RequestBody List<OrderDetailsDTO> detailsRequest) {

        log.info("Controller: Received request to update order {}", id);

        OrderResponseDTO updatedOrder = orderService.updateOrder(id, detailsRequest);

        SuccessResponseDTO<OrderResponseDTO> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("Order %d updated successfully", id),
                updatedOrder
        );

        log.info("Controller: Order {} updated successfully", id);
        return ResponseEntity.ok(response);
    }
}
