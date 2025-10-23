package org.technoready.meliecommerce.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.technoready.meliecommerce.controller.OrderController;
import org.technoready.meliecommerce.dto.OrderDetailsDTO;
import org.technoready.meliecommerce.dto.OrderResponseDTO;
import org.technoready.meliecommerce.entity.Order;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.service.OrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("Order Controller Integration Tests")
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "auto-test.enabled=false"
})

class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private Order testOrder;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Jorge")
                .lastName("Avila")
                .email("jorge@example.com")
                .active(true)
                .build();

        testOrder = Order.builder()
                .id(1L)
                .user(testUser)
                .total(28999.99)
                .active(true)
                .details(new ArrayList<>())
                .build();
    }

    // SUCCESS CASES
    @Test
    @DisplayName("POST /api/orders/{userId} - Should create order successfully")
    void testCreateOrder_Success() throws Exception {
        // Arrange
        OrderDetailsDTO detailsDTO = new OrderDetailsDTO(1L, 2);
        List<OrderDetailsDTO> detailsList = List.of(detailsDTO);

        when(orderService.createOrder(eq(1L), any())).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailsList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("GET /api/orders - Should retrieve all orders successfully")
    void testGetAllOrders_Success() throws Exception {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderService.getAllActiveOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders")
                        .param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Should retrieve order by ID successfully")
    void testGetOrderById_Success() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - Should delete order successfully")
    void testDeleteOrder_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT /api/orders/{id} - Should update order successfully")
    void testUpdateOrder_Success() throws Exception {
        // Arrange
        OrderDetailsDTO detailsDTO = new OrderDetailsDTO(1L, 3);
        List<OrderDetailsDTO> detailsList = List.of(detailsDTO);

        OrderResponseDTO responseDTO = OrderResponseDTO.builder()
                .id(1L)
                .userId(1L)
                .total(86999.97)
                .active(true)
                .createdAt(LocalDateTime.now())
                .details(new ArrayList<>())
                .build();

        when(orderService.updateOrder(eq(1L), any())).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailsList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // FAILURE SCENARIOS
    @Test
    @DisplayName("POST /api/orders/{userId} - Should return 404 when user not found")
    void testCreateOrder_UserNotFound() throws Exception {
        // Arrange
        OrderDetailsDTO detailsDTO = new OrderDetailsDTO(1L, 2);
        List<OrderDetailsDTO> detailsList = List.of(detailsDTO);

        when(orderService.createOrder(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("User", "id", 999L));

        // Act & Assert
        mockMvc.perform(post("/api/orders/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detailsList)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Should return 404 when order not found")
    void testGetOrderById_NotFound() throws Exception {
        // Arrange
        when(orderService.getOrderById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }

    // EDGE CASES
    @Test
    @DisplayName("GET /api/orders - Should return empty list when no orders exist")
    void testGetAllOrders_EmptyList() throws Exception {
        // Arrange
        when(orderService.getAllActiveOrders()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET /api/orders/user/{userId} - Should retrieve orders for specific user")
    void testGetOrdersByUser_Success() throws Exception {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderService.getOrdersByUserIdActive(1L)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}