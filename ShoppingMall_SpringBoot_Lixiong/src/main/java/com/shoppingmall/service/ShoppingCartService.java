package com.shoppingmall.service;

import com.shoppingmall.entity.ShoppingCart;
import com.shoppingmall.entity.Products;
import com.shoppingmall.repository.ShoppingCartRepository;
import com.shoppingmall.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class ShoppingCartService {
    
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<ShoppingCart> getCartByUsername(String username) {
        return shoppingCartRepository.findByUsername(username);
    }
    
    public List<Map<String, Object>> getCartWithProducts(String username) {
        List<ShoppingCart> carts = shoppingCartRepository.findByUsernameWithProduct(username);
        return carts.stream().map(cart -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cart.getId());
            map.put("username", cart.getUsername());
            map.put("productId", cart.getProductId());
            map.put("quantity", cart.getQuantity());
            map.put("createdAt", cart.getCreatedAt());
            
            Optional<Products> product = productRepository.findById(cart.getProductId());
            product.ifPresent(p -> {
                map.put("productName", p.getProductName());
                map.put("price", p.getPrice());
                map.put("category", p.getCategory());
                map.put("imageUrl", p.getImageUrl());
            });
            return map;
        }).toList();
    }
    
    public List<Map<String, Object>> getHistoryByUsernameAndCategory(String username, String category) {
        List<ShoppingCart> carts;
        if ("ALL".equals(category) || category == null) {
            carts = shoppingCartRepository.findByUsernameWithProduct(username);
        } else {
            carts = shoppingCartRepository.findByUsernameAndCategory(username, category);
        }
        return carts.stream().map(cart -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cart.getId());
            map.put("username", cart.getUsername());
            map.put("productId", cart.getProductId());
            map.put("quantity", cart.getQuantity());
            map.put("createdAt", cart.getCreatedAt());
            
            Optional<Products> product = productRepository.findById(cart.getProductId());
            product.ifPresent(p -> {
                map.put("productName", p.getProductName());
                map.put("price", p.getPrice());
                map.put("category", p.getCategory());
                map.put("imageUrl", p.getImageUrl());
            });
            return map;
        }).toList();
    }
    
    @Transactional
    public Map<String, Object> addToCart(String username, Long productId, Integer quantity) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<Products> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            result.put("success", false);
            result.put("message", "商品不存在");
            return result;
        }
        
        Products product = productOpt.get();
        if (product.getStockQuantity() < quantity) {
            result.put("success", false);
            result.put("message", "库存不足，当前剩余数量：" + product.getStockQuantity());
            return result;
        }
        
        product.setStockQuantity(product.getStockQuantity() - quantity);
        product.setUpdatedAt(java.time.LocalDateTime.now());
        productRepository.save(product);
        
        ShoppingCart cart = new ShoppingCart();
        cart.setUsername(username);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setCreatedAt(java.time.LocalDateTime.now());
        shoppingCartRepository.save(cart);
        
        result.put("success", true);
        result.put("message", "购买成功");
        return result;
    }
}