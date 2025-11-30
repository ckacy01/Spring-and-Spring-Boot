package org.technoready.meliecommerce.service_unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.technoready.meliecommerce.dto.OrderDetailsDTO;
import org.technoready.meliecommerce.dto.OrderResponseDTO;
import org.technoready.meliecommerce.entity.Order;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.exception.InactiveResourceException;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.repository.OrderRepository;
import org.technoready.meliecommerce.repository.ProductRepository;
import org.technoready.meliecommerce.repository.UserRepository;
import org.technoready.meliecommerce.service.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Unit Tests")
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "auto-test.enabled=false"
})
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Jorge")
                .lastName("Avila")
                .email("jorge@example.com")
                .active(true)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Laptop Gamer ASUS")
                .description("Laptop con RTX 4060")
                .price(28999.99)
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
    @DisplayName("Should create order successfully with valid data")
    void testCreateOrder_Success() {
        // Arrange
        OrderDetailsDTO detailsDTO = new OrderDetailsDTO(1L, 2);
        List<OrderDetailsDTO> detailsList = List.of(detailsDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(1L, detailsList);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should retrieve all orders successfully")
    void testGetAllOrders_Success() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve order by ID successfully")
    void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Optional<Order> result = orderService.getOrderById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should delete order successfully (soft delete)")
    void testDeleteOrder_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order successfully")
    void testUpdateOrder_Success() {
        // Arrange
        OrderDetailsDTO detailsDTO = new OrderDetailsDTO(1L, 3);
        List<OrderDetailsDTO> detailsList = List.of(detailsDTO);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderResponseDTO result = orderService.updateOrder(1L, detailsList);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // FAILURE SCENARIOS
    @Test
    @DisplayName("Should throw exception when creating order with invalid user ID")
    void testCreateOrder_UserNotFound() {
        // Arrange
        OrderDetailsDTO detailsDTO = new OrderDetailsDTO(1L, 2);
        List<OrderDetailsDTO> detailsList = List.of(detailsDTO);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(999L, detailsList);
        });
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when creating order with invalid product ID")
    void testCreateOrder_ProductNotFound() {
        // Arrange
        OrderDetailsDTO detailsDTO = new OrderDetailsDTO(999L, 2);
        List<OrderDetailsDTO> detailsList = List.of(detailsDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(1L, detailsList);
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent order")
    void testDeleteOrder_OrderNotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.deleteOrder(999L);
        });
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when updating inactive order")
    void testUpdateOrder_InactiveOrder() {
        // Arrange
        testOrder.setActive(false);
        OrderDetailsDTO detailsDTO = new OrderDetailsDTO(1L, 2);
        List<OrderDetailsDTO> detailsList = List.of(detailsDTO);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThrows(InactiveResourceException.class, () -> {
            orderService.updateOrder(1L, detailsList);
        });
    }

    // EDGE CASES
    @Test
    @DisplayName("Should handle empty order details list")
    void testCreateOrder_EmptyDetailsList() {
        // Arrange
        List<OrderDetailsDTO> emptyList = new ArrayList<>();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(1L, emptyList);

        // Assert
        assertNotNull(result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void testGetAllOrders_EmptyList() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Order> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty optional when order not found")
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderService.getOrderById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should retrieve active orders for user successfully")
    void testGetOrdersByUserIdActive_Success() {
        // Arrange
        List<Order> orders = List.of(testOrder);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUser_IdAndActiveTrue(1L)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getOrdersByUserIdActive(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByUser_IdAndActiveTrue(1L);
    }
}
