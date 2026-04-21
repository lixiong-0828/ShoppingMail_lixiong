package com.shoppingmall.repository;

import com.shoppingmall.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    
    @Query("SELECT p FROM Products p WHERE (:category IS NULL OR :category = 'ALL' OR p.category = :category)")
    List<Products> findByCategory(@Param("category") String category);
    
    Optional<Products> findByCategoryAndProductName(String category, String productName);
    
    boolean existsByCategoryAndProductName(String category, String productName);
}