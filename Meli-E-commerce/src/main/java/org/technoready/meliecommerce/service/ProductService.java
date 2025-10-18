package org.technoready.meliecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.exception.ResourceNotFoundException;
import org.technoready.meliecommerce.repository.ProductRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        log.info("Retrieving all products");
        List<Product> products = productRepository.findAll();
        log.info("Retrieved {} products", products.size());
        return products;
    }

    public List<Product> findAllByIsActiveTrue() {
        log.info("Retrieving all active products");
        List<Product> products = productRepository.findByActiveTrue();
        log.info("Retrieved {} active products", products.size());
        return products;
    }

    public Product findById(Long id) {
        log.info("Retrieving product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product", "id", id);
                });
    }

    public Product save(Product product) {
        log.info("Creating new product: {}", product.getName());
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        return savedProduct;
    }

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
