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
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.repository.ProductRepository;
import org.technoready.meliecommerce.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Unit Tests")
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "auto-test.enabled=false"
})
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Laptop Gamer ASUS")
                .description("Laptop con RTX 4060 y 16GB RAM")
                .price(28999.99)
                .active(true)
                .build();
    }

    // SUCCESS CASES
    @Test
    @DisplayName("Should retrieve all products successfully")
    void testFindAll_Success() {
        // Arrange
        List<Product> products = List.of(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop Gamer ASUS", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve all active products successfully")
    void testFindAllByIsActiveTrue_Success() {
        // Arrange
        List<Product> activeProducts = List.of(testProduct);
        when(productRepository.findByActiveTrue()).thenReturn(activeProducts);

        // Act
        List<Product> result = productService.findAllByIsActiveTrue();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(productRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Should find product by ID successfully")
    void testFindById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop Gamer ASUS", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should save product successfully")
    void testSave_Success() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product result = productService.save(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop Gamer ASUS", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product successfully (soft delete)")
    void testDelete_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        productService.delete(1L);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdate_Success() {
        // Arrange
        Product updatedProduct = Product.builder()
                .name("Updated Laptop")
                .description("Updated description")
                .price(29999.99)
                .active(true)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.update(updatedProduct, 1L);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // FAILURE SCENARIOS
    @Test
    @DisplayName("Should throw exception when product not found by ID")
    void testFindById_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(999L);
        });
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void testDelete_ProductNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(999L);
        });
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void testUpdate_ProductNotFound() {
        // Arrange
        Product updatedProduct = Product.builder()
                .name("Updated Laptop")
                .build();

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(updatedProduct, 999L);
        });
        verify(productRepository, times(1)).findById(999L);
    }

    // EDGE CASES
    @Test
    @DisplayName("Should return empty list when no products exist")
    void testFindAll_EmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Product> result = productService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no active products exist")
    void testFindAllByIsActiveTrue_EmptyList() {
        // Arrange
        when(productRepository.findByActiveTrue()).thenReturn(new ArrayList<>());

        // Act
        List<Product> result = productService.findAllByIsActiveTrue();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle product with null price")
    void testSave_NullPrice() {
        // Arrange
        Product productWithNullPrice = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(null)
                .active(true)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(productWithNullPrice);

        // Act
        Product result = productService.save(productWithNullPrice);

        // Assert
        assertNotNull(result);
        assertNull(result.getPrice());
    }
}
