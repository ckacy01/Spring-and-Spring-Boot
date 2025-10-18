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
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.service.OrderService;
import org.technoready.meliecommerce.util.MapperUtil;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<SuccessResponseDTO<List<OrderResponseDTO>>> getOrdersByUser(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {

        String message;
        SuccessResponseDTO<List<OrderResponseDTO>> response = new SuccessResponseDTO<>();

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
