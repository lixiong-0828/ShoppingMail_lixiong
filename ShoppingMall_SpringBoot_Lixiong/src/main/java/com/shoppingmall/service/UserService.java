package com.shoppingmall.service;

import com.shoppingmall.entity.Users;
import com.shoppingmall.repository.UserRepository;
import com.shoppingmall.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public Map<String, Object> login(String username, String password) {
        Users user = userRepository.findByUserNameEn(username).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            String token = jwtUtil.generateToken(username, user.getRole());
            return Map.of(
                "success", true,
                "token", token,
                "username", username,
                "role", user.getRole(),
                "userNameCh", user.getUserNameCh()
            );
        }
        return Map.of("success", false, "message", "Invalid username or password");
    }
    
    public List<Users> searchUsers(String userName, String role) {
        return userRepository.searchUsers(userName, role);
    }
    
    public Map<String, Object> createUser(Users user) {
        if (userRepository.existsByUserNameEn(user.getUserNameEn())) {
            return Map.of("success", false, "message", "Username already exists");
        }
        userRepository.save(user);
        return Map.of("success", true, "message", "User created successfully");
    }
    
    public Users getUserByUsername(String username) {
        return userRepository.findByUserNameEn(username).orElse(null);
    }
}