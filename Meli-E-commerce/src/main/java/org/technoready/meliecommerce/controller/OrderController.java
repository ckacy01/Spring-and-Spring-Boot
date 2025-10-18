package org.technoready.meliecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.technoready.meliecommerce.dto.OrderDetailsDTO;
import org.technoready.meliecommerce.dto.OrderResponseDTO;
import org.technoready.meliecommerce.entity.Order;
import org.technoready.meliecommerce.service.OrderService;
import org.technoready.meliecommerce.util.MapperUtil;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @PathVariable Long userId,
            @RequestBody List<OrderDetailsDTO> detailsRequest) {

        Order order = orderService.createOrder(userId, detailsRequest);
        return ResponseEntity.ok(MapperUtil.toDTO(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {

        List<Order> orders = activeOnly
                ? orderService.getAllActiveOrders()
                : orderService.getAllOrders();

        return ResponseEntity.ok(MapperUtil.toDTOList(orders));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUser(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {

        List<Order> orders = activeOnly
                ? orderService.getOrdersByUserIdActive(userId)
                : orderService.getOrdersByUserId(userId);

        return ResponseEntity.ok(MapperUtil.toDTOList(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(MapperUtil.toDTO(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody List<OrderDetailsDTO> detailsRequest) {

        OrderResponseDTO updatedOrder = orderService.updateOrder(id, detailsRequest);
        return ResponseEntity.ok(updatedOrder);
    }
}
