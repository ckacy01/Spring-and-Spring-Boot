package org.technoready.meliecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.technoready.meliecommerce.entity.Product;
import org.technoready.meliecommerce.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAllByIsActiveTrue() {
        return productRepository.findByActiveTrue();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    public Product update(Product product, Long id) {
        Product product1 = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product1.setName(product.getName());
        product1.setPrice(product.getPrice());
        product1.setDescription(product.getDescription());
        product1.setActive(product.isActive());
        productRepository.save(product);
        return product1;
    }
}
