package com.shoppingmall.controller;

import com.shoppingmall.service.ShoppingCartService;
import com.shoppingmall.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class ShoppingCartController {
    
    @Autowired
    private ShoppingCartService shoppingCartService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false, defaultValue = "ALL") String category) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }
        String jwt = token.substring(7);
        String username = jwtUtil.getUsernameFromToken(jwt);
        
        List<Map<String, Object>> history = shoppingCartService.getHistoryByUsernameAndCategory(username, category);
        return ResponseEntity.ok(Map.of("success", true, "data", history));
    }
    
    @GetMapping("/history-all")
    public ResponseEntity<Map<String, Object>> getHistoryAll(
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }
        String jwt = token.substring(7);
        String role = jwtUtil.getRoleFromToken(jwt);
        
        if (!"Admin".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Forbidden"));
        }
        
        List<Map<String, Object>> history = shoppingCartService.getAllHistory();
        return ResponseEntity.ok(Map.of("success", true, "data", history));
    }
    
    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buy(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> request) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }
        String jwt = token.substring(7);
        String username = jwtUtil.getUsernameFromToken(jwt);
        
        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        Map<String, Object> result = shoppingCartService.addToCart(username, productId, quantity);
        
        if (Boolean.TRUE.equals(result.get("success"))) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}