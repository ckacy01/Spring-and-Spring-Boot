package org.technoready.meliecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.technoready.meliecommerce.dto.SuccessResponseDTO;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.service.ProductService;

import java.util.List;

/**
 * REST Controller that manages product-related operations.
 * Provides endpoints for retrieving, creating, updating, and deleting products.
 * <p>
 * DATE: 22 - October - 2025
 *
 * @author
 * Jorge Armando Avila Carrillo | NAOID: 3310
 * @version 1.3
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "Endpoints for managing products in the e-commerce platform")
public class ProductController {

    private final ProductService productService;

    /**
     * Retrieves all products or only active products based on the {@code activeOnly} parameter.
     *
     * @param activeOnly boolean - Flag to retrieve only active products (default: true)
     * @return ResponseEntity with SuccessResponseDTO containing a list of Products
     */
    @Operation(
            summary = "Retrieve all products",
            description = "Fetches a list of all products. Optionally filters by active products only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SuccessResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<Product>>> findAll(
            @Parameter(description = "Flag to retrieve only active products", example = "true")
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

    /**
     * Retrieves a specific product by its ID.
     *
     * @param id long - The ID of the product to retrieve
     * @return ResponseEntity with SuccessResponseDTO containing the Product
     * @throws ResourceNotFoundException if the product is not found
     */
    @Operation(
            summary = "Retrieve a product by ID",
            description = "Fetches the details of a single product using its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SuccessResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<Product>> findById(
            @Parameter(description = "ID of the product to retrieve", example = "1")
            @PathVariable long id) {

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

    /**
     * Soft deletes (deactivates) a product by its ID.
     *
     * @param id Long - The ID of the product to delete
     * @return ResponseEntity with SuccessResponseDTO indicating successful deactivation
     * @throws ResourceNotFoundException if the product is not found
     */
    @Operation(
            summary = "Soft delete a product",
            description = "Deactivates a product by setting its active flag to false."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deactivated successfully",
                    content = @Content(schema = @Schema(implementation = SuccessResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<Void>> delete(
            @Parameter(description = "ID of the product to delete", example = "1")
            @PathVariable Long id) {

        log.info("Controller: Received request to delete product {}", id);

        productService.delete(id);

        SuccessResponseDTO<Void> response = SuccessResponseDTO.of(
                HttpStatus.OK.value(),
                String.format("Product %d has been successfully deactivated", id)
        );

        log.info("Controller: Product {} deleted successfully", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new product with the provided details.
     *
     * @param product Product - The product object to be created
     * @return ResponseEntity with SuccessResponseDTO containing the created Product
     */
    @Operation(
            summary = "Create a new product",
            description = "Creates a new product using the provided JSON body."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = SuccessResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<SuccessResponseDTO<Product>> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details for creation",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Product.class)))
            @RequestBody Product product) {

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

    /**
     * Updates an existing product with new details.
     *
     * @param id Long - The ID of the product to update
     * @param product Product - The product object with updated information
     * @return ResponseEntity with SuccessResponseDTO containing the updated Product
     * @throws ResourceNotFoundException if the product is not found
     */
    @Operation(
            summary = "Update an existing product",
            description = "Updates a product's information using its ID and the provided details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = SuccessResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<Product>> update(
            @Parameter(description = "ID of the product to update", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Product.class)))
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
