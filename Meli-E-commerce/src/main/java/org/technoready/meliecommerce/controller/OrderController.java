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
@RequestMapping("/api/order")
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
        public ResponseEntity<List<Order>> getAllOrders() {
            return ResponseEntity.ok(orderService.getAllOrders());
        }

        @GetMapping("/{id}")
        public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
            return orderService.getOrderById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
}

