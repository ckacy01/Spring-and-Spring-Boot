package org.technoready.meliecommerce.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.technoready.meliecommerce.controller.ProductController;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.service.ProductService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Integration Tests")
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "auto-test.enabled=false"
})
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Laptop Gamer ASUS")
                .description("Laptop con RTX 4060")
                .price(28999.99)
                .active(true)
                .build();
    }

    // SUCCESS CASES (R28)
    @Test
    @DisplayName("GET /api/products - Should retrieve all products successfully")
    void testFindAll_Success() throws Exception {
        // Arrange
        List<Product> products = List.of(testProduct);
        when(productService.findAllByIsActiveTrue()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Laptop Gamer ASUS"));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should retrieve product by ID successfully")
    void testFindById_Success() throws Exception {
        // Arrange
        when(productService.findById(1L)).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Laptop Gamer ASUS"));
    }

    @Test
    @DisplayName("POST /api/products - Should create product successfully")
    void testSave_Success() throws Exception {
        // Arrange
        when(productService.save(any(Product.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.name").value("Laptop Gamer ASUS"));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should update product successfully")
    void testUpdate_Success() throws Exception {
        // Arrange
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Laptop")
                .description("Updated description")
                .price(29999.99)
                .active(true)
                .build();

        when(productService.update(any(Product.class), eq(1L))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Updated Laptop"));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should delete product successfully")
    void testDelete_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists());
    }

    // FAILURE SCENARIOS (R26)
    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 when product not found")
    void testFindById_NotFound() throws Exception {
        // Arrange
        when(productService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Product", "id", 999L));

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should return 404 when updating non-existent product")
    void testUpdate_NotFound() throws Exception {
        // Arrange
        when(productService.update(any(Product.class), eq(999L)))
                .thenThrow(new ResourceNotFoundException("Product", "id", 999L));

        // Act & Assert
        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isNotFound());
    }

    // EDGE CASES (R27)
    @Test
    @DisplayName("GET /api/products - Should return empty list when no products exist")
    void testFindAll_EmptyList() throws Exception {
        // Arrange
        when(productService.findAllByIsActiveTrue()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET /api/products?activeOnly=false - Should retrieve all products including inactive")
    void testFindAll_IncludeInactive() throws Exception {
        // Arrange
        Product inactiveProduct = Product.builder()
                .id(2L)
                .name("Inactive Product")
                .active(false)
                .build();

        List<Product> allProducts = List.of(testProduct, inactiveProduct);
        when(productService.findAll()).thenReturn(allProducts);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .param("activeOnly", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}
