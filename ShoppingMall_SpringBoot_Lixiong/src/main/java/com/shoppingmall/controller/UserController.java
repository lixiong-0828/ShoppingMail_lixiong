package com.shoppingmall.controller;

import com.shoppingmall.entity.Users;
import com.shoppingmall.service.UserService;
import com.shoppingmall.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    public ResponseEntity<List<Users>> searchUsers(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String role) {
        List<Users> users = userService.searchUsers(userName, role);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Users user) {
        Map<String, Object> result = userService.createUser(user);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{username}")
    public ResponseEntity<Users> getUserByUsername(@PathVariable String username) {
        Users user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}