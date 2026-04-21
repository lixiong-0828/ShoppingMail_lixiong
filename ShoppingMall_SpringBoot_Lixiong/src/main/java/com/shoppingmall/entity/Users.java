package com.shoppingmall.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class Users {
    @Id
    @Column(name = "user_name_en", length = 50)
    private String userNameEn;

    @Column(name = "user_name_ch", nullable = false, length = 100)
    private String userNameCh;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String role = "Customer";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}