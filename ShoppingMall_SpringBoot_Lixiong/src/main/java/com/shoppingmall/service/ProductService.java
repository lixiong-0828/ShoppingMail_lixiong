package com.shoppingmall.service;

import com.shoppingmall.entity.Products;
import com.shoppingmall.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Products> getAllProducts(String category) {
        if (category == null || category.equals("ALL") || category.isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByCategory(category);
    }
    
    public Map<String, Object> createProduct(Products product) {
        if (productRepository.existsByCategoryAndProductName(product.getCategory(), product.getProductName())) {
            return Map.of("success", false, "message", "Product name already exists in this category");
        }
        productRepository.save(product);
        return Map.of("success", true, "message", "Product created successfully");
    }
    
    public Map<String, Object> updateProduct(Long id, Products product) {
        Optional<Products> existingProduct = productRepository.findById(id);
        if (existingProduct.isEmpty()) {
            return Map.of("success", false, "message", "Product not found");
        }
        
        Products p = existingProduct.get();
        if (!p.getCategory().equals(product.getCategory()) || !p.getProductName().equals(product.getProductName())) {
            if (productRepository.existsByCategoryAndProductName(product.getCategory(), product.getProductName())) {
                return Map.of("success", false, "message", "Product name already exists in this category");
            }
        }
        
        p.setProductName(product.getProductName());
        p.setPrice(product.getPrice());
        p.setStockQuantity(product.getStockQuantity());
        p.setCategory(product.getCategory());
        p.setImageUrl(product.getImageUrl());
        productRepository.save(p);
        
        return Map.of("success", true, "message", "Product updated successfully");
    }
    
    public Map<String, Object> deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            return Map.of("success", false, "message", "Product not found");
        }
        productRepository.deleteById(id);
        return Map.of("success", true, "message", "Product deleted successfully");
    }
    
    public Products getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}