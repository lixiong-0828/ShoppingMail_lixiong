package com.shoppingmall.repository;

import com.shoppingmall.entity.ShoppingCart;
import com.shoppingmall.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    
    List<ShoppingCart> findByUsername(String username);
    
    @Query("SELECT sc FROM ShoppingCart sc JOIN Products p ON sc.productId = p.id WHERE sc.username = :username AND (:category = 'ALL' OR p.category = :category)")
    List<ShoppingCart> findByUsernameAndCategory(@Param("username") String username, @Param("category") String category);
    
    @Query("SELECT sc FROM ShoppingCart sc JOIN Products p ON sc.productId = p.id WHERE sc.username = :username")
    List<ShoppingCart> findByUsernameWithProduct(@Param("username") String username);
    
    boolean existsByUsernameAndProductId(String username, Long productId);
}