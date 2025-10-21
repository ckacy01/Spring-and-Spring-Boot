package org.technoready.meliecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.repository.ProductRepository;

import java.util.List;

/**
 * Service class that handles business logic for product operations.
 * Manages product retrieval, creation, updating, and deletion.
 * DATE: 18 - October - 2025
 *
 * @author Jorge Armando Avila Carrillo | NAOID: 3310
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    /**
     * Retrieves all products (active and inactive).
     *
     * @return List<Product> - List of all products
     */
    public List<Product> findAll() {
        log.info("Retrieving all products");
        List<Product> products = productRepository.findAll();
        log.info("Retrieved {} products", products.size());
        return products;
    }


    /**
     * Retrieves only active products.
     *
     * @return List<Product> - List of active products
     */
    public List<Product> findAllByIsActiveTrue() {
        log.info("Retrieving all active products");
        List<Product> products = productRepository.findByActiveTrue();
        log.info("Retrieved {} active products", products.size());
        return products;
    }

    /**
     * Retrieves a specific product by its ID.
     *
     * @param id Long - The ID of the product
     * @return Product - The product entity
     * @throws ResourceNotFoundException if product is not found
     */
    public Product findById(Long id) {
        log.info("Retrieving product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });
    }

    /**
     * Creates and saves a new product.
     *
     * @param product Product - The product to save
     * @return Product - The saved product with generated ID
     */
    public Product save(Product product) {
        log.info("Creating new product: {}", product.getName());
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return savedProduct;
    }


    /**
     * Soft deletes a product by deactivating it.
     *
     * @param id Long - The ID of the product to delete
     * @throws ResourceNotFoundException if product is not found
     */
    public void delete(Long id) {
        log.info("Attempting to delete product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot delete - Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });

        product.setActive(false);
        productRepository.save(product);
        log.info("Product with id: {} has been successfully deactivated", id);
    }


    /**
     * Updates an existing product with new information.
     *
     * @param product Product - The product with updated data
     * @param id Long - The ID of the product to update
     * @return Product - The updated product entity
     * @throws ResourceNotFoundException if product is not found
     */
    public Product update(Product product, Long id) {
        log.info("Attempting to update product with id: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot update - Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setActive(product.isActive());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product with id: {} has been successfully updated", id);

        return updatedProduct;
    }
}
