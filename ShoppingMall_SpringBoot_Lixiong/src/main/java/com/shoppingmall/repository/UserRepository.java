package com.shoppingmall.repository;

import com.shoppingmall.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByUserNameEn(String userNameEn);
    
    @Query("SELECT u FROM Users u WHERE (:userName IS NULL OR :userName = '' OR u.userNameEn LIKE %:userName% OR u.userNameCh LIKE %:userName%) " +
           "AND (:role IS NULL OR :role = '' OR u.role = :role)")
    List<Users> searchUsers(@Param("userName") String userName, @Param("role") String role);
    
    boolean existsByUserNameEn(String userNameEn);
}