package org.technoready.meliecommerce.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.technoready.meliecommerce.dto.SuccessResponseDTO;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<Product>>> findAll(
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {

        log.info("Controller: Received request to get all products (activeOnly: {})", activeOnly);

        List<Product> products = activeOnly
                ? productService.findAllByIsActiveTrue()
                : productService.findAll();

        String message = activeOnly
                ? String.format("Retrieved %d active products successfully", products.size())
                : String.format("Retrieved %d products successfully", products.size());

        SuccessResponseDTO<List<Product>> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                message,
                products
        );

        log.info("Controller: Retrieved {} products", products.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<Product>> findById(@PathVariable long id) {
        log.info("Controller: Received request to get product {}", id);

        Product product = productService.findById(id);

        SuccessResponseDTO<Product> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("Product %d retrieved successfully", id),
                product
        );

        log.info("Controller: Product {} retrieved successfully", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<Void>> delete(@PathVariable Long id) {
        log.info("Controller: Received request to delete product {}", id);

        productService.delete(id);

        SuccessResponseDTO<Void> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("Product %d has been successfully deactivated", id)
        );

        log.info("Controller: Product {} deleted successfully", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<Product>> save(@RequestBody Product product) {
        log.info("Controller: Received request to create product {}", product.getName());

        Product savedProduct = productService.save(product);

        SuccessResponseDTO<Product> response = SuccessResponseDTO.of(
                HttpStatus.CREATED.value(),
                String.format("Product '%s' created successfully with ID: %d",
                        savedProduct.getName(), savedProduct.getId()),
                savedProduct
        );

        log.info("Controller: Product {} created successfully", savedProduct.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<Product>> update(
            @PathVariable Long id,
            @RequestBody Product product) {

        log.info("Controller: Received request to update product {}", id);

        Product updatedProduct = productService.update(product, id);

        SuccessResponseDTO<Product> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("Product %d updated successfully", id),
                updatedProduct
        );

        log.info("Controller: Product {} updated successfully", id);
        return ResponseEntity.ok(response);
    }

}
