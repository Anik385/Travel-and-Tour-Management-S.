package com.example.Travel.repository;

import com.example.Travel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Existing methods
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // New methods for AdminService
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    Long countActiveUsers();



    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    // FIX: Use Pageable for limiting results, as JPQL doesn't support 'LIMIT'
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(org.springframework.data.domain.Pageable pageable);
}
