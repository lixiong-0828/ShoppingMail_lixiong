package com.shoppingmall.controller;

import com.shoppingmall.entity.Products;
import com.shoppingmall.service.ProductService;
import com.shoppingmall.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    public ResponseEntity<List<Products>> getAllProducts(@RequestParam(required = false, defaultValue = "ALL") String category) {
        List<Products> products = productService.getAllProducts(category);
        return ResponseEntity.ok(products);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Products product) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }
        String jwt = token.substring(7);
        String role = jwtUtil.getRoleFromToken(jwt);
        if (!"Admin".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Admin access required"));
        }
        Map<String, Object> result = productService.createProduct(product);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id,
            @RequestBody Products product) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }
        String jwt = token.substring(7);
        String role = jwtUtil.getRoleFromToken(jwt);
        if (!"Admin".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Admin access required"));
        }
        Map<String, Object> result = productService.updateProduct(id, product);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }
        String jwt = token.substring(7);
        String role = jwtUtil.getRoleFromToken(jwt);
        if (!"Admin".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Admin access required"));
        }
        Map<String, Object> result = productService.deleteProduct(id);
        return ResponseEntity.ok(result);
    }
}